package com.aem.smart.utils.hc;

import com.google.common.collect.Sets;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.api.Result;
import org.apache.sling.hc.util.FormattingResultLog;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Default abstract class for runmode aware health checks. All subclasses are run just on specified set of runmodes.
 */
@Component(componentAbstract = true)
@Properties({
        @Property(
                name = RunmodeAwareHealthCheck.ENABLED_ON_RUNMODES_PROPERTY,
                cardinality = Integer.MAX_VALUE,
                label = "Runmodes to run this healthchek on"
        ),
        @Property(
                name = RunmodeAwareHealthCheck.SITE_NAMES,
                cardinality = Integer.MAX_VALUE,
                label = "Site names",
                description = "Set sitename folder where runmodes could be found, e.g. /apps/sitename/runmodes."

        )
})
public abstract class AbstractRunmodeAwareHealthCheck implements RunmodeAwareHealthCheck {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String[] DEFAULT_ENABLED_ON_RUNMODES = new String[] { RUNMODE_AUTHOR, RUNMODE_PUBLISH };

    @Reference
    private SlingSettingsService slingSettingsService;

    private Set<String> enabledOnRunmodes;
    private Set<String> currentRunModes;
    private Set<String> siteNames;
    private boolean enabled;

    @Activate
    public void activate(Map<String, Object> properties) {
        Objects.requireNonNull(slingSettingsService, "No reference to SlingSettingsService");

        this.enabledOnRunmodes = Sets.newHashSet(PropertiesUtil.toStringArray(
                properties.get(ENABLED_ON_RUNMODES_PROPERTY),
                DEFAULT_ENABLED_ON_RUNMODES
        ));

        this.siteNames = Sets.newHashSet(PropertiesUtil.toStringArray(
                properties.get(SITE_NAMES)));

        this.currentRunModes = Sets.newHashSet(slingSettingsService.getRunModes());
        for (String runmode : enabledOnRunmodes) {
            if (currentRunModes.contains(runmode)) {
                enabled = true;
            }
        }
    }

    /**
     * Execute this health check and return a {@link Result}
     * This is meant to execute quickly, access to external
     * systems, for example, should be managed asynchronously.
     */
    @Override
    public final Result execute() {
        final FormattingResultLog resultLog = new FormattingResultLog();
        resultLog.info(getEnabledMessage());
        if (enabled) {
            for (String siteName : siteNames) {
                execute(siteName, resultLog);
            }
        }
        return new Result(resultLog);
    }

    @Override
    public final Set<String> getEnabledOnRunmodes() {
        return Sets.newHashSet(enabledOnRunmodes);
    }

    protected abstract void execute(String siteName, FormattingResultLog resultLog);

    protected final Logger getLogger() {
        return logger;
    }

    protected final Set<String> getCurrentRunModes() {
        return Sets.newHashSet(currentRunModes);
    }

    private String getEnabledMessage() {
        return enabled ? String.format("ENABLED on '%s'", enabledOnRunmodes) : "DISABLED";
    }
}
