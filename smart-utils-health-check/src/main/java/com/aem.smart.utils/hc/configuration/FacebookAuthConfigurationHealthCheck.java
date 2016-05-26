package com.aem.smart.utils.hc.configuration;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.hc.annotations.SlingHealthCheck;

/**
 *
 */
@SlingHealthCheck(
        name = "Facebook OAuth Proxy Configuration Health Check",
        label = "Facebook OAuth Proxy Configuration Health Check",
        description = "Checks facebook oauth proxy configuration for validity",
        tags = { "atmosphere", "configuration" }
)
public class FacebookAuthConfigurationHealthCheck extends AbstractConfigurationHealthCheck {

    @Override
    public String getServicePid() {
        return "com.fglsports.wcm.rest.services.FacebookAuthProxyServlet";
    }

    @Override
    public void checkConfiguration(ConfigurationCheckContext context) {

        String host = context.getStringProperty("inner.facebook.auth.host");
        if (StringUtils.isBlank(host)) {
            context.getResultLog().warn("\tInvalid Facebook OAuth Proxy Server Address");
        } else {
            context.getResultLog().debug("\tFacebook OAuth Proxy Server Address '{}' is OK", host);
        }

        int propertyInt = context.getIntegerProperty("inner.facebook.auth.port");
        if (propertyInt <= 0) {
            context.getResultLog().warn("\tInvalid Facebook OAuth Proxy Server Port '{}'", propertyInt);
        } else {
            context.getResultLog().debug("\tFacebook OAuth Proxy Server Port '{}' is OK", propertyInt);
        }

        String location = context.getStringProperty("inner.facebook.auth.location");
        if (StringUtils.isBlank(location)) {
            context.getResultLog().warn("\tInvalid Facebook OAuth Location");
        } else {
            context.getResultLog().debug("\tFacebook OAuth Location '{}' is OK", location);
        }

        String banner = context.getStringProperty("inner.facebook.auth.banner");
        if (StringUtils.isBlank(banner)) {
            context.getResultLog().warn("\tInvalid Facebook OAuth Banner");
        } else {
            context.getResultLog().debug("\tFacebook OAuth Banner '{}' is OK", banner);
        }

        if (!location.contains(banner)) {
            context.getResultLog().warn("Facebook OAuth Location '{}' does not contain Banner '{}'!", location, banner);
        }
    }

    @Override
    public String getHealthCheckToken() {
        return "Facebook OAuth proxy configuration";
    }
}
