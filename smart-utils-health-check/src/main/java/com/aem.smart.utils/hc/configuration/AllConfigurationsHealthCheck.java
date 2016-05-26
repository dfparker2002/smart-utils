package com.aem.smart.utils.hc.configuration;

import com.aem.smart.utils.hc.configuration.util.FglConfiguration;
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

    protected void execute(FormattingResultLog resultLog) {

        Multimap<String, FglConfiguration> runmodesConfigurationsMap = getRunmodeConfigurationsLoader()
                .loadConfigurations(resultLog);

        Multimap<String, FglConfiguration> actualConfigurationsMap = getActualConfigurationsLoader()
                .loadConfigurations(runmodesConfigurationsMap.keySet(), resultLog);

        for (String servicePid : actualConfigurationsMap.keySet()) {
            Collection<FglConfiguration> actualProperties = actualConfigurationsMap.get(servicePid);
            Collection<FglConfiguration> runmodeProperties = runmodesConfigurationsMap.get(servicePid);
            Map<String, Collection<Object>> runmodePropertiesMap = toMultimap(runmodeProperties).asMap();

            PropertiesLogger propertiesLogger = new PropertiesLogger(servicePid);

            for (FglConfiguration actualConfiguration : actualProperties) {
                Map<String, Object> actualPropertiesMap = actualConfiguration.getProperties();
                checkMatches(actualPropertiesMap, runmodePropertiesMap, propertiesLogger);
            }

            resultLog.debug(propertiesLogger.log());
        }
    }

}