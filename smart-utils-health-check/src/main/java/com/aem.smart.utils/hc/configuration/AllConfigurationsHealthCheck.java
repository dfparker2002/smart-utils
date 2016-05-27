package com.aem.smart.utils.hc.configuration;

import com.aem.smart.utils.hc.configuration.util.SiteConfiguration;
import com.aem.smart.utils.hc.configuration.util.PropertiesLogger;
import com.google.common.collect.Multimap;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;

import java.util.Collection;
import java.util.Map;

/**
 * Check particular service configurations against ones specified in runmodes.
 */
@SlingHealthCheck(
        name = "All Configuration Health Check",
        label = "All Configuration Health Check",
        description = "Checks all configurations to conform ones from runmodes",
        tags = { "configuration" }
)
public class AllConfigurationsHealthCheck extends AbstractExtendedConfigurationsHealthCheck {

    protected void execute(String siteName, FormattingResultLog resultLog) {

        Multimap<String, SiteConfiguration> runmodesConfigurationsMap = getRunmodeConfigurationsLoader()
                .loadConfigurations(siteName, resultLog);

        Multimap<String, SiteConfiguration> actualConfigurationsMap = getActualConfigurationsLoader()
                .loadConfigurations(siteName, runmodesConfigurationsMap.keySet(), resultLog);

        for (String servicePid : actualConfigurationsMap.keySet()) {
            Collection<SiteConfiguration> actualProperties = actualConfigurationsMap.get(servicePid);
            Collection<SiteConfiguration> runmodeProperties = runmodesConfigurationsMap.get(servicePid);
            Map<String, Collection<Object>> runmodePropertiesMap = toMultimap(runmodeProperties).asMap();

            PropertiesLogger propertiesLogger = new PropertiesLogger(servicePid);

            for (SiteConfiguration actualConfiguration : actualProperties) {
                Map<String, Object> actualPropertiesMap = actualConfiguration.getProperties();
                checkMatches(actualPropertiesMap, runmodePropertiesMap, propertiesLogger);
            }

            resultLog.debug(propertiesLogger.log());
        }
    }

}