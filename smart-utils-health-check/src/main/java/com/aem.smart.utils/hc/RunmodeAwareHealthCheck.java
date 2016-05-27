package com.aem.smart.utils.hc;

import org.apache.sling.hc.api.HealthCheck;

import java.util.Set;

/**
 * Interface for runmode aware health checks.
 */
public interface RunmodeAwareHealthCheck extends HealthCheck {

    /**
     * Author runmode constant.
     */
    String RUNMODE_AUTHOR = "author";

    /**
     * Publish runmode constant.
     */
    String RUNMODE_PUBLISH = "publish";

    /**
     * Property name.
     */
    String ENABLED_ON_RUNMODES_PROPERTY = "enabled.on.runmodes";

    /**
     * Property name.
     */
    String SITE_NAMES = "site.names";

    /**
     * @return set of runmodes to run this health check on. If the list is empty, than health check won't be run at all.
     */
    Set<String> getEnabledOnRunmodes();
}
