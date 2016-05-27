package com.aem.smart.utils.hc.configuration;

import com.aem.smart.utils.hc.configuration.util.SiteConfiguration;
import com.aem.smart.utils.hc.configuration.util.PropertiesLogger;
import com.aem.smart.utils.hc.configuration.util.ReplicationAgentsConfigurationsLogger;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Checks provided mandatory configurations.
 */
@SlingHealthCheck(
        name = "Mandatory Configuration Health Check",
        label = "Mandatory Configuration Health Check",
        description = "Checks Mandatory configurations to conform ones from runmodes",
        tags = { "configuration" }
)
public class MandatoryConfigurationsHealthCheck extends AbstractExtendedConfigurationsHealthCheck {
    //TODO: should be configured, without project names
    @Property(
            cardinality = Integer.MAX_VALUE,
            value = {},
            label = "Configurations to check"
    )
    private static final String CONFIGURATIONS_TO_CHECK_PROPERTY = "configurations.to.check";

    @Reference
    private ReplicationAgentsConfigurationsLogger replicationAgentsConfigurationsLogger;

    private String[] configurationsToCheck = ArrayUtils.EMPTY_STRING_ARRAY;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);
        Objects.requireNonNull(replicationAgentsConfigurationsLogger, "No reference to ReplicationAgentsConfigurationsLoader");

        this.configurationsToCheck = PropertiesUtil.toStringArray(
                properties.get(CONFIGURATIONS_TO_CHECK_PROPERTY), ArrayUtils.EMPTY_STRING_ARRAY
        );
    }

    protected void execute(String siteName, FormattingResultLog resultLog) {

        Multimap<String, SiteConfiguration> runmodesConfigurationsMap = getRunmodeConfigurationsLoader()
                .loadConfigurations(siteName, Arrays.asList(configurationsToCheck), resultLog);

        Multimap<String, SiteConfiguration> actualConfigurationsMap = getActualConfigurationsLoader()
                .loadConfigurations(siteName, Arrays.asList(configurationsToCheck), resultLog);

        for (String servicePid : configurationsToCheck) {
            Collection<SiteConfiguration> actualProperties = actualConfigurationsMap.get(servicePid);
            Collection<SiteConfiguration> runmodeProperties = runmodesConfigurationsMap.get(servicePid);
            Map<String, Collection<Object>> runmodePropertiesMap = toMultimap(runmodeProperties).asMap();

            PropertiesLogger propertiesLogger = new PropertiesLogger(servicePid);

            if (actualProperties.isEmpty()) {
                resultLog.warn(propertiesLogger.log());
                continue;
            }

            for (SiteConfiguration actualConfiguration : actualProperties) {
                Map<String, Object> actualPropertiesMap = actualConfiguration.getProperties();
                checkMatches(actualPropertiesMap, runmodePropertiesMap, propertiesLogger);
            }

            resultLog.info(propertiesLogger.log());
        }

        replicationAgentsConfigurationsLogger.logProperties(resultLog);
    }

}
