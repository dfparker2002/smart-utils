package com.aem.smart.utils.hc.configuration;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.hc.annotations.SlingHealthCheck;

/**
 *
 */
@SlingHealthCheck(
        name = "Hybris Configuration Health Check",
        label = "Hybris Configuration Health Check",
        description = "Checks hybris configuration for validity",
        tags = { "atmosphere", "configuration" }
)
public class HybrisConfigurationHealthCheck extends AbstractConfigurationHealthCheck {

    @Override
    public String getServicePid() {
        return "com.fglsports.wcm.site.rest.impl.HybrisRestConnectionConfigImpl";
    }

    @Override
    public String getHealthCheckToken() {
        return "hybris configuration";
    }

    @Override
    public void checkConfiguration(ConfigurationCheckContext context) {

        String propertyString = context.getStringProperty("hybris.server.address");
        if (StringUtils.isBlank(propertyString)) {
            context.getResultLog().warn("\tInvalid Hybris Server Address");
        } else {
            context.getResultLog().debug("\tHybris Server Address '{}' is OK", propertyString);
        }

        int propertyInt = context.getIntegerProperty("hybris.server.port");
        if (propertyInt <= 0) {
            context.getResultLog().warn("\tInvalid Hybris Server Port '{}'", propertyInt);
        } else {
            context.getResultLog().debug("\tHybris Server Port '{}' is OK", propertyInt);
        }

        propertyInt = context.getIntegerProperty("hybris.server.secure.port");
        if (propertyInt <= 0) {
            context.getResultLog().warn("\tInvalid Hybris Secure Server Port '{}'", propertyInt);
        } else {
            context.getResultLog().debug("\tHybris Secure Server Port '{}' is OK", propertyInt);
        }

        propertyString = context.getStringProperty("hybris.server.admin.username");
        if (StringUtils.isBlank(propertyString)) {
            context.getResultLog().warn("\tBlank Hybris Admin Username");
        } else {
            context.getResultLog().debug("\tHybris Admin Username is OK");
        }

        propertyString = context.getStringProperty("hybris.server.admin.password");
        if (StringUtils.isBlank(propertyString)) {
            context.getResultLog().warn("\tBlank Hybris Admin Password");
        } else {
            context.getResultLog().debug("\tHybris Admin Password is OK");
        }

        String[] propertyArray = context.getStringArrayProperty("hybris.server.store_independent_prefixes");
        if (ArrayUtils.isEmpty(propertyArray)) {
            context.getResultLog().warn("\tInvalid Hybris Store Independent Prefixes");
        } else {
            context.getResultLog().debug("\tHybris Store Independent Prefixes are OK");
        }

        propertyString = context.getStringProperty("hybris.services.prefix.default");
        if (StringUtils.isEmpty(propertyString)) {
            context.getResultLog().warn("\tBlank Hybris Default Prefix");
        } else {
            context.getResultLog().debug("\tHybris Default Prefix '{}' is OK", propertyString);
        }

        propertyInt = context.getIntegerProperty("hybris.http.timeout.connection");
        if (propertyInt <= 0) {
            context.getResultLog().warn("\tInvalid Http Connection Timeout '{}'", propertyInt);
        } else {
            context.getResultLog().debug("\tHttp Connection Timeout '{}' is OK", propertyInt);
        }

        propertyInt = context.getIntegerProperty("hybris.http.timeout.socket");
        if (propertyInt <= 0) {
            context.getResultLog().warn("\tInvalid Socket Connection Timeout '{}'", propertyInt);
        } else {
            context.getResultLog().debug("\tSocket Connection Timeout '{}' is OK", propertyInt);
        }

    }

}
