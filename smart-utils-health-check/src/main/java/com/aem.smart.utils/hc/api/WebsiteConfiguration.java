package com.aem.smart.utils.hc.api;

import java.util.Collection;

/**
 *
 * The interface Website configuration.
 * You should implement this interface that Health Check can pick up it for test.
 *
 */
public interface WebsiteConfiguration {

    /**
     * Gets configurations for.
     *
     * @return the configurations
     */
    Collection<WebsiteConfiguration> getConfigurations();

    /**
     * Gets website name.
     *
     * @return the website name
     */
    String getWebsiteName();

    /**
     * Gets hosts.
     *
     * @return the hosts
     */
    Collection<String> getHosts();

    /**
     * Gets content path.
     *
     * @return the content path
     */
    Collection<String> getContentPath();

    /**
     * Gets catalog path.
     *
     * @return the catalog path
     */
    String getCatalogPath();

    /**
     * Gets tags path.
     *
     * @return the tags path
     */
    String getTagsPath();
}
