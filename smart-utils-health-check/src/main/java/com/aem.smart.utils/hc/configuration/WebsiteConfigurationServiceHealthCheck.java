package com.aem.smart.utils.hc.configuration;

import com.aem.smart.utils.commons.api.WebsiteConfiguration;
import com.aem.smart.utils.hc.AbstractRunmodeAwareHealthCheck;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;

import java.util.Collection;

/**
 *
 */
@SlingHealthCheck(
        name = "Website Configuration Service Health Check",
        mbeanName = "websiteConfigHC",
        description = "Website Configuration Service Health Check",
        tags = {"atmosphere", "configuration"}
)
public class WebsiteConfigurationServiceHealthCheck extends AbstractRunmodeAwareHealthCheck {

    @Reference
    private WebsiteConfiguration websiteConfiguration;

    @Override
    protected void execute(FormattingResultLog resultLog) {
        new WebsiteConfigurationChecker(resultLog)
                .checkWebsiteConfigurations(websiteConfiguration.getConfigurations());
    }
}

class WebsiteConfigurationChecker {

    final FormattingResultLog resultLog;

    public WebsiteConfigurationChecker(FormattingResultLog resultLog) {
        this.resultLog = resultLog;
    }

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

    public void checkWebsiteConfiguration(WebsiteConfiguration websiteConfiguration) {
        resultLog.debug("Found configuration '{}'", websiteConfiguration.getWebsiteName());
        checkHosts(websiteConfiguration);
        //TODO : add universal checker based on interfaces that user could implement own checker
      //  checkHybrisBaseSite(websiteConfiguration);
      //  checkHybrisBaseCatalog(websiteConfiguration);
       // checkOnlineStoreId(websiteConfiguration);
    }

    private void checkIfBlank(String value, String warnMessage, String debugMessage) {
        if (StringUtils.isBlank(value)) {
            resultLog.warn(warnMessage);
        } else {
            resultLog.debug(debugMessage, value);
        }
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

//    private void checkOnlineStoreId(WebsiteConfiguration websiteConfiguration) {
//        checkIfBlank(websiteConfiguration.getOnlineStoreId(),
//                     "\tInvalid configuration for Online Store Id",
//                     "\tOnline Store Id '{}' is OK"
//        );
//    }
//
//    private void checkHybrisBaseCatalog(WebsiteConfiguration websiteConfiguration) {
//        checkIfBlank(websiteConfiguration.getHybrisBaseCatalog(),
//                     "\tInvald configuration for Hybris Base Catalog",
//                     "\tHybris Base Catalog '{}' is OK"
//        );
//    }
//
//    private void checkHybrisBaseSite(WebsiteConfiguration websiteConfiguration) {
//        checkIfBlank(websiteConfiguration.getHybrisBaseSite(),
//                     "\tInvalid configurations for Hybris Base Site",
//                     "\tHybris Base Site '{}' is OK"
//        );
//    }
}