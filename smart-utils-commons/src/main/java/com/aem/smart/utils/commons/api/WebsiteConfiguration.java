package com.aem.smart.utils.commons.api;

import java.util.Collection;

public interface WebsiteConfiguration {

    Collection<WebsiteConfiguration> getConfigurations();

    String getWebsiteName();

    Collection<String> getHosts();

    Collection<String> getContentPath();

    String getCatalogPath();

    String getTagsPath();
}
