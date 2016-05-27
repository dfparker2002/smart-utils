package com.aem.smart.utils.hc.configuration.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.hc.util.FormattingResultLog;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Loads current OSGi service configs.
 */
@Component(immediate = true, label = "Actual configurations loader")
@Service(ActualConfigurationsLoader.class)
public class ActualConfigurationsLoader implements ConfigurationLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualConfigurationsLoader.class);

    @Reference
    private ConfigurationAdmin configurationAdmin;

    @Activate
    public void activate(Map<String, Object> properties) {
        Objects.requireNonNull(configurationAdmin, "No reference to ConfigurationAdmin");
    }

    @Override
    public Multimap<String, SiteConfiguration> loadConfigurations(String siteName, FormattingResultLog resultLog) {
        throw new UnsupportedOperationException("This method is not supported by this implementation!");
    }

    @Override
    public Multimap<String, SiteConfiguration> loadConfigurations(String siteName,
                                                                  Collection<String> servicePids,
                                                                  FormattingResultLog resultLog) {

        Configuration[] configurations = loadConfigurationsInner();
        Multimap<String, SiteConfiguration> actualConfigurationsMap = ArrayListMultimap.create();
        for (String servicePid : servicePids) {
            Collection<SiteConfiguration> siteConfigurations =
                    loadActualConfigurationsForService(configurations, servicePid, resultLog);
            actualConfigurationsMap.putAll(servicePid, siteConfigurations);
        }
        return actualConfigurationsMap;
    }

    private Configuration[] loadConfigurationsInner() {
        Configuration[] result = new Configuration[0];
        try {
            result = configurationAdmin.listConfigurations(null);

        } catch (IOException | InvalidSyntaxException ex) {
            LOGGER.error("Failed to list configurations", ex);
        }
        return result;
    }

    private Collection<SiteConfiguration> loadActualConfigurationsForService(Configuration[] configurations,
                                                                             String servicePid, FormattingResultLog resultLog) {

        Collection<SiteConfiguration> siteConfigurations = new ArrayList<>();
        try {
            Collection<Configuration> filteredConfigurations = findConfigurations(configurations, servicePid);

            for (final Configuration configuration : filteredConfigurations) {
                if (null == configuration) {
                    resultLog.warn("Cannot find configuration for pid '{}'", servicePid);
                    continue;
                }
                Dictionary properties = configuration.getProperties();
                if (null == properties) {
                    resultLog.warn("Cannot read properties for pid '{}'", servicePid);
                    continue;
                }

                SiteConfiguration siteConfiguration = new SiteConfiguration(servicePid);
                Enumeration keysEnumeration = properties.keys();
                while (keysEnumeration.hasMoreElements()) {
                    String key = String.valueOf(keysEnumeration.nextElement());
                    siteConfiguration.storeProperty(key, properties.get(key));
                }
                siteConfigurations.add(siteConfiguration);
            }

        } catch (Exception ex) {
            LOGGER.error("Failed to check '{}' configurations: ", servicePid, ex);
            resultLog.warn("Failed to check '{}' configurations: [{}]", servicePid, ex);
        }

        return siteConfigurations;
    }

    private Collection<Configuration> findConfigurations(Configuration[] configurations, String pid) {
        List<Configuration> result = new ArrayList<>();
        for (Configuration configuration : configurations) {
            if (configuration.getPid().contains(pid)) {
                result.add(configuration);
            }
        }
        return result;
    }

}
