package com.aem.smart.utils.hc.configuration;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.hc.annotations.SlingHealthCheck;

/**
 *
 */
@SlingHealthCheck(
        name = "Externalizer Configuration Health Check",
        label = "Externalizer Configuration Health Check",
        description = "Externalizer Configuration Health Check",
        tags = { "cq", "configuration" }
)
public class ExternalizerConfigurationHealthCheck extends AbstractConfigurationHealthCheck {

    @Override
    public String getServicePid() {
        return "com.day.cq.commons.impl.ExternalizerImpl";
    }

    @Override
    public String getHealthCheckToken() {
        return "externalizer";
    }

    @Override
    public void checkConfiguration(ConfigurationCheckContext context) {
        String[] domains = context.getStringArrayProperty("externalizer.domains");

        if (ArrayUtils.isEmpty(domains)) {
            context.getResultLog().warn("No domains have been found for '{}'", getServicePid());
            return;
        }

        boolean localIsPresent = false;
        boolean authorIsPresent = false;
        boolean publishIsPresent = false;
        boolean dispatcherIsPresent = false;

        for (String domain : domains) {

            if (StringUtils.isBlank(domain)) {
                context.getResultLog().warn("Invalid domain configuration '{}'", domain);
                continue;
            }

            String[] tokens = domain.trim().split(" ");

            if (ArrayUtils.isEmpty(tokens) || tokens.length < 2) {
                context.getResultLog().warn("Invalid domain configuration '{}'", domain);

            } else {

                localIsPresent = localIsPresent || "local".equalsIgnoreCase(tokens[0]);
                authorIsPresent = authorIsPresent || "author".equalsIgnoreCase(tokens[0]);
                publishIsPresent = publishIsPresent || "publish".equalsIgnoreCase(tokens[0]);
                dispatcherIsPresent = dispatcherIsPresent || "dispatcher_sportchek".equalsIgnoreCase(tokens[0]);

                if (checkDomainParameterName(tokens[0])) {
                    context.getResultLog().warn("Invalid domain configuration '{}'", domain);
                } else {
                    context.getResultLog().debug("Checked domain configuration '{}'", domain);
                }
            }
        }

        if (!localIsPresent) {
            context.getResultLog().warn("No domain configuration found for 'local'");
        }
        if (!authorIsPresent) {
            context.getResultLog().warn("No domain configuration found for 'author'");
        }
        if (!publishIsPresent) {
            context.getResultLog().warn("No domain configuration found for 'publish'");
        }
        if (!dispatcherIsPresent) {
            context.getResultLog().warn("No domain configuration found for 'dispatcher_sportchek'");
        }

        context.getResultLog().info("Checked '{}' domain configurations out of 4", domains.length);
    }

    private boolean checkDomainParameterName(String domain) {
        return StringUtils.isNotBlank(domain) && !"local".equalsIgnoreCase(domain) && !"author".equalsIgnoreCase(domain) &&
               !"publish".equalsIgnoreCase(domain) && !"dispatcher_sportchek".equalsIgnoreCase(domain);
    }
}
