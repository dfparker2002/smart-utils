package com.aem.smart.utils.hc.configuration.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jcr.RepositoryException;
import javax.jcr.query.QueryManager;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.hc.util.FormattingResultLog;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.smart.utils.commons.jcr.SessionHolder;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Loads configurations from current runmode(-s).
 */
@Component(immediate = true, label = "Runmode configurations loader")
@Service(RunmodesConfigurationLoader.class)
public class RunmodesConfigurationLoader implements ConfigurationLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunmodesConfigurationLoader.class);

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    private SlingRepository slingRepository;

    @Activate
    public void activate(Map<String, Object> properties) {
        Objects.requireNonNull(slingRepository, "No reference to SlingRepository");
        Objects.requireNonNull(slingSettingsService, "No reference to SlingSettingsService");
    }

    @Override
    public Multimap<String, SiteConfiguration> loadConfigurations(String siteName, FormattingResultLog resultLog) {
        return loadConfigurationsInner(siteName, new EmptyRunmodeConfigurationsFilter(), resultLog);
    }

    @Override
    public Multimap<String, SiteConfiguration> loadConfigurations(String siteName, Collection<String> servicePids,
            FormattingResultLog resultLog) {

        return loadConfigurationsInner(siteName, new SimpleRunmodeConfigurationsFilter(servicePids), resultLog);
    }

    private String getCurrentRunmodeName() {
        Set<String> runmodes = slingSettingsService.getRunModes();

        LOGGER.debug("Runmodes from SlingSettingsService: '{}'", runmodes);

        Set<String> resultSet = new LinkedHashSet<>();
        resultSet.add("config");

        Set<String> filteredSet = runmodes.stream()
                .filter(runmode -> null != runmode && !runmode.contains("samplecontent") && !runmode.contains("crx"))
                .collect(Collectors.toSet());

        moveRunmodeIfPresent("author", filteredSet, resultSet);
        moveRunmodeIfPresent("publish", filteredSet, resultSet);

        resultSet.addAll(filteredSet);

        return Joiner.on(".").join(resultSet);
    }

    private Multimap<String, SiteConfiguration> loadConfigurationsInner(String siteName,
            RunmodeConfigurationsFilter filter, FormattingResultLog resultLog) {

        Multimap<String, SiteConfiguration> configurationsMap = ArrayListMultimap.create();

        String currentRunmode = getCurrentRunmodeName();

        if (!currentRunmode.startsWith("config.author") && !currentRunmode.startsWith("config.publish")) {
            resultLog.warn("Invalid runmode '{}'!", currentRunmode);
            LOGGER.warn("Invalid runmode '{}'!", currentRunmode);
            return configurationsMap;

        } else {
            resultLog.info("Determined current runmode as '{}'", currentRunmode);
            LOGGER.debug("Determined current runmode as '{}'", currentRunmode);

        }

        try (SessionHolder holder = new SessionHolder(slingRepository)) {
            QueryManager queryManager = holder.getSession().getWorkspace().getQueryManager();

            RunmodesConfigurationLoaderHelper configurationLoaderHelper = new RunmodesConfigurationLoaderHelper(
                    siteName, queryManager, resultLog);

            Iterator<String> runmodeNameIterator = new RunmodeNamesHierarchicalIterator(currentRunmode);
            while (runmodeNameIterator.hasNext()) {
                String runmodeName = runmodeNameIterator.next();
                if (StringUtils.isNotBlank(runmodeName)) {
                    configurationLoaderHelper.loadConfigurationsForRunmode(runmodeName, filter);
                }
            }

            configurationsMap = configurationLoaderHelper.getFglConfigurationsMap();

        } catch (RepositoryException ex) {
            LOGGER.error("Failed to get runmodes configurations", ex);

        }

        return configurationsMap;
    }

    private void moveRunmodeIfPresent(String value, Set<String> sourceSet, Set<String> targetSet) {
        if (sourceSet.contains(value)) {
            targetSet.add(value);
            boolean result = sourceSet.remove(value);
            if (!result) {
                LOGGER.warn("Failed to remove '{}' from '{}'", value, sourceSet);
            }
        }
    }

    /**
     * Empty filter implementation.
     */
    private static class EmptyRunmodeConfigurationsFilter implements RunmodeConfigurationsFilter {

        public boolean apply(String input) {
            return true;
        }
    }

    /**
     * Simple filter implementation.
     */
    private static class SimpleRunmodeConfigurationsFilter implements RunmodeConfigurationsFilter {

        private final Collection<String> servicePids;

        SimpleRunmodeConfigurationsFilter(Collection<String> servicePids) {
            this.servicePids = servicePids;
        }

        public boolean apply(String input) {
            return null != input && servicePids.contains(input);
        }
    }
}
