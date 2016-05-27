package com.aem.smart.utils.hc.api;

/**
 *
 * The interface Hybris rest connection config.
 * You should implement this interface that Health Check can pick up it for test.
 *
 */
public interface HybrisRestConnectionConfig {

    /**
     * Gets base url of Hybris endpoints.
     *
     * @return the base url
     */
    String getBaseUrl();
}
