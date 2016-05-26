package com.aem.smart.utils.hc.configuration.util;

import com.google.common.collect.Multimap;
import org.apache.sling.hc.util.FormattingResultLog;

import java.util.Collection;

/**
 * Common interface for configuration loader (either runmode or OSGi configurations).
 */
public interface ConfigurationLoader {
    /**
     * Loads all configurations as multimap, where configuration name (service pid) is used as a key.
     *
     * @param resultLog health check log
     * @return multimap of configurations.
     */
    Multimap<String, FglConfiguration> loadConfigurations(FormattingResultLog resultLog);

    /**
     * Loads configurations as multimap for provided configuration names (service pids).
     *
     * @param resultLog health check log
     * @return multimap of configurations.
     */
    Multimap<String, FglConfiguration> loadConfigurations(Collection<String> servicePids, FormattingResultLog resultLog);
}
