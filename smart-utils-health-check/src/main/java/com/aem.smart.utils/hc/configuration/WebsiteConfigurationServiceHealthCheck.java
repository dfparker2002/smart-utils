package com.aem.smart.utils.hc.configuration;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;

import com.aem.smart.utils.hc.AbstractRunmodeAwareHealthCheck;
import com.aem.smart.utils.hc.api.WebsiteConfiguration;

/**
 * The type Website configuration service health check.
 */
@SlingHealthCheck(name = "Website Configuration Service Health Check", mbeanName = "websiteConfigHC", description = "Website Configuration Service Health Check", tags = {
        "configuration" })
public class WebsiteConfigurationServiceHealthCheck extends AbstractRunmodeAwareHealthCheck {

    @Reference
    private WebsiteConfiguration websiteConfiguration;

    @Override
    protected void execute(String siteName, FormattingResultLog resultLog) {
        new WebsiteConfigurationChecker(resultLog).checkWebsiteConfigurations(websiteConfiguration.getConfigurations());
    }
}

/**
 * The type Website configuration checker.
 */
class WebsiteConfigurationChecker {

    /**
     * The Result log.
     */
    final FormattingResultLog resultLog;

    /**
     * Instantiates a new Website configuration checker.
     *
     * @param resultLog the result log
     */
    public WebsiteConfigurationChecker(FormattingResultLog resultLog) {
        this.resultLog = resultLog;
    }

    /**
     * Check website configurations.
     *
     * @param websiteConfigurations the website configurations
     */
    public void checkWebsiteConfigurations(Collection<WebsiteConfiguration> websiteConfigurations) {
        if (CollectionUtils.isEmpty(websiteConfigurations)) {
            resultLog.warn("No website configurations were found!");
        } else {
            resultLog.info("Found '{}' website configurations", websiteConfigurations.size());
            for (final WebsiteConfiguration websiteConfiguration : websiteConfigurations) {
                checkWebsiteConfiguration(websiteConfiguration);
            }
        }
    }

    /**
     * Check website configuration.
     *
     * @param websiteConfiguration the website configuration
     */
    public void checkWebsiteConfiguration(WebsiteConfiguration websiteConfiguration) {
        resultLog.debug("Found configuration '{}'", websiteConfiguration.getWebsiteName());
        checkHosts(websiteConfiguration);
    }

    private void checkHosts(WebsiteConfiguration websiteConfiguration) {
        Collection<String> hosts = websiteConfiguration.getHosts();
        if (CollectionUtils.isEmpty(hosts)) {
            resultLog.warn("\tNo hosts are configured");
        } else {
            for (String host : hosts) {
                if (StringUtils.isBlank(host)) {
                    resultLog.warn("\tInvalid configuration for host '{}'", host);
                } else {
                    resultLog.debug("\tHost '{}' is OK", host);
                }
            }
        }
    }
}