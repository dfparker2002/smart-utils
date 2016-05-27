package com.aem.smart.utils.hc.api;

/**
 *
 * The interface Search and Promote configuration.
 * You should implement this interface that Health Check can pick up it for test.
 *
 */
public interface SearchAndPromoteConfiguration {

    /**
     * Gets server url.
     *
     * @return the server url
     */
    String getServerUrl();
}
