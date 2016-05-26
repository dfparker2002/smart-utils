package com.aem.smart.utils.hc.configuration;

import com.aem.smart.utils.hc.RunmodeAwareHealthCheck;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.hc.annotations.SlingHealthCheck;

/**
 * Checks mail configurations.
 */
@SlingHealthCheck(
        name = "Day CQ Mail Service Configuration Health Check",
        label = "Day CQ Mail Service Configuration Health Check",
        description = "Day CQ Mail Service Configuration Health Check",
        tags = { "cq", "mail", "configuration" }
)
@Properties({
        @Property(
                name = RunmodeAwareHealthCheck.ENABLED_ON_RUNMODES_PROPERTY,
                value = { RunmodeAwareHealthCheck.RUNMODE_AUTHOR },
                cardinality = Integer.MAX_VALUE,
                label = "Runmodes to run this healthchek on"
        )
})
public class MailServiceConfigurationHealthCheck extends AbstractConfigurationHealthCheck {

    @Override
    public String getServicePid() {
        return "com.day.cq.mailer.DefaultMailService";
    }

    @Override
    public void checkConfiguration(ConfigurationCheckContext context) {
        String propertyString = context.getStringProperty("smtp.host");
        if (StringUtils.isBlank(propertyString)) {
            context.getResultLog().warn("\tSMTP Server Host is not set");
        } else {
            context.getResultLog().debug("\tSMTP Server Host '{}' is OK", propertyString);
        }

        propertyString = context.getStringProperty("smtp.port");
        if (StringUtils.isBlank(propertyString)) {
            context.getResultLog().warn("\tSMTP Server Port is not set");
        } else {
            context.getResultLog().debug("\tSMTP Server Port '{}' is OK", propertyString);
        }

        propertyString = context.getStringProperty("smtp.user");
        if (StringUtils.isBlank(propertyString)) {
            context.getResultLog().warn("\tSMTP User is not set");
        } else {
            context.getResultLog().debug("\tSMTP User '{}' is OK", propertyString);
        }

        propertyString = context.getStringProperty("smtp.password");
        if (StringUtils.isBlank(propertyString)) {
            context.getResultLog().warn("\tSMTP Password is not set");
        } else {
            context.getResultLog().debug("\tSMTP Password is OK", propertyString);
        }
    }

    @Override
    public String getHealthCheckToken() {
        return "mail service";
    }
}
