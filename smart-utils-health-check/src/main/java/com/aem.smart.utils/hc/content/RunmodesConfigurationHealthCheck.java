package com.aem.smart.utils.hc.content;

import com.aem.smart.utils.commons.jcr.ResolverHolder;
import com.aem.smart.utils.hc.AbstractRunmodeAwareHealthCheck;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;
import org.apache.sling.query.SlingQuery;
import org.osgi.framework.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
@SlingHealthCheck(
        name = "Runmodes configuration consistency Health Check",
        label = "Runmodes configuration consistency Health Check",
        description = "Checks runmodes configurations for relevance. If configuration name, i.e. fully qualified name " +
                "is not present among OSGi components, it's assumed that config is not actual and can/should be deleted. " +
                "NOTE: If some components are in 'unsatisfied' state and this component was declared as configuration " +
                "factory, healthcheck most probably won't be able to identify that and will complain that factory " +
                "config may be missing. In this case check components status.",
        tags = { "atmosphere", "consistency", "configuration" }
)
public class RunmodesConfigurationHealthCheck extends AbstractRunmodeAwareHealthCheck {

    private static final String RUNMODES_PATH = "/apps/sportchek/runmodes";
    private static final String RUNMODE_CONFIF_RESOURCE_TYPE = "sling:OsgiConfig";
    private static final String CONFIG = "config";
    private static final String AUTHOR = "author";
    private static final String PUBLISH = "publish";

    @Property(
            cardinality = Integer.MAX_VALUE,
            value = {
                    "org.apache.felix.http",
                    "org.apache.sling.commons.log",
                    "com.fglsports.saui"
            },
            label = "Runmode configurations to skip"
    )
    private static final String RUNMODE_CONFIGURATIONS_TO_SKIP_PROPERTY = "runmode.configurations.to.skip";

    @Property(
            cardinality = Integer.MAX_VALUE,
            value = {
                    "atmo"
            },
            label = "Runmodes to check",
            description = "If the list is not empty, just runmodes containing provided ones will be checked. If the " +
                    "list is empty, all runmodes will be checked."
    )
    private static final String RUNMODES_TO_CHECK_PROPERTY = "runmodes.to.check";

    private String[] runmodeConfigurationsToSkip = ArrayUtils.EMPTY_STRING_ARRAY;
    private String[] runmodesToCheck = ArrayUtils.EMPTY_STRING_ARRAY;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ScrService scrService;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);

        Objects.requireNonNull(resourceResolverFactory, "No reference to ResourceResolverFactory");
        Objects.requireNonNull(scrService, "No reference to ScrService");

        this.runmodeConfigurationsToSkip = PropertiesUtil.toStringArray(
                properties.get(RUNMODE_CONFIGURATIONS_TO_SKIP_PROPERTY), ArrayUtils.EMPTY_STRING_ARRAY
        );
        this.runmodesToCheck = PropertiesUtil.toStringArray(
                properties.get(RUNMODES_TO_CHECK_PROPERTY), ArrayUtils.EMPTY_STRING_ARRAY
        );
    }

    @Override
    protected void execute(FormattingResultLog log) {

        try (ResolverHolder resolver = new ResolverHolder(resourceResolverFactory)) {

            Map<String, ComponentInfo> componentNameToServicePidMap = createComponentNameToServicePidMap();

            Resource resource = resolver.getResolver().getResource(RUNMODES_PATH);
            Set<String> runmodes = getCurrentRunModes();

            if (null == resource) {
                log.warn("Path '{}' does not exist", RUNMODES_PATH);

            } else {
                final SlingQuery query = SlingQuery.$(resource).find(RUNMODE_CONFIF_RESOURCE_TYPE);
                int count = 0;
                for (Resource pathResource : query) {
                    ComponentPresenceConsistencyCheckContext context =
                            new CustomConsistencyCheckContext(pathResource, componentNameToServicePidMap, log);
                    checkConsistencyInner(context, runmodes);
                    ++count;
                }
                if (count > 0) {
                    log.info("Checked '{}' {}s in '{}'", count, getLogToken(), RUNMODES_PATH);
                } else {
                    log.warn("No {}s have been found in '{}'", getLogToken(), RUNMODES_PATH);
                }
            }
        }

    }

    private Map<String, ComponentInfo> createComponentNameToServicePidMap() {
        Map<String, ComponentInfo> componentNameToServicePidMap = new HashMap<>();
        Component[] components = scrService.getComponents();
        for (Component component : components) {
            componentNameToServicePidMap.put(component.getName(), new ComponentInfo(component));
        }
        return componentNameToServicePidMap;
    }

    private void checkConsistencyInner(ComponentPresenceConsistencyCheckContext context, Set<String> runmodes) {
        try {
            String resourcePath = context.getResourcePath();
            String configurationName = StringUtils.substringAfterLast(resourcePath, "/");
            String runmodeName = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(resourcePath, "/"), "/");

            if (shouldSkipRunmode(runmodeName, runmodes) || shouldSkipConfiguration(configurationName)) {
                return;
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Checking runmode '{}'", runmodeName);
            }

            if (!context.componentExists(configurationName)) {
                context.getResultLog().warn("Not valid configuration for '{}' in '{}'", configurationName, runmodeName);
            }

        } catch (Exception ex) {
            context.getResultLog().warn("Exception occurred: '{}'", ex.getMessage());
        }
    }

    private boolean shouldSkipRunmode(String runmodeName, Set<String> runmodes) {
        boolean result = !runmodeName.contains(CONFIG) ||
                !runmodeName.contains(AUTHOR) && !runmodeName.contains(PUBLISH) ||
                runmodeName.contains(AUTHOR) && !runmodes.contains(AUTHOR) ||
                runmodeName.contains(PUBLISH) && !runmodes.contains(PUBLISH);

        for (String runmodeToCheck : runmodesToCheck) {
            result = result || !runmodeName.contains(runmodeToCheck);
        }
        return result;
    }

    private boolean shouldSkipConfiguration(String configurationName) {
        for (String runmodeConfigurationToSkip : runmodeConfigurationsToSkip) {
            if (configurationName.startsWith(runmodeConfigurationToSkip)) {
                return true;
            }
        }
        return false;
    }

    private String getLogToken() {
        return "runmodes configuration";
    }

    /**
     * Simple context interface that provides handy methods for consistency checking.
     */
    interface ComponentPresenceConsistencyCheckContext extends ConsistencyCheckContext {

        boolean componentExists(String componentName);
    }

    /**
     * Class that holds some data about {@link org.apache.felix.scr.Component} to be used in {@link com.fglsports.hc.content.ConsistencyCheckContext}
     */
    static final class ComponentInfo {

        private final String servicePid;
        private final int state;

        public ComponentInfo(Component component) {
            this.servicePid = String.valueOf(component.getProperties().get(Constants.SERVICE_PID));
            this.state = component.getState();
        }

        public String getServicePid() {
            return servicePid;
        }

        public int getState() {
            return state;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ComponentInfo that = (ComponentInfo) o;

            if (state != that.state) {
                return false;
            }
            if (servicePid != null ? !servicePid.equals(that.servicePid) : that.servicePid != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = servicePid != null ? servicePid.hashCode() : 0;
            result = 31 * result + state;
            return result;
        }
    }

    /**
     * Custom implementation of {@link ComponentPresenceConsistencyCheckContext}
     */
    static class CustomConsistencyCheckContext extends DefaultConsistencyCheckContext
            implements ComponentPresenceConsistencyCheckContext {

        private final Map<String, ComponentInfo> componentNameToServicePidMap;

        public CustomConsistencyCheckContext(Resource currentResource, Map<String, ComponentInfo> map, FormattingResultLog resultLog) {
            super(currentResource, resultLog);
            this.componentNameToServicePidMap = map;
        }

        @Override
        public boolean componentExists(String componentName) {
            boolean result = false;

            if (StringUtils.isNotBlank(componentName)) {
                if (componentName.contains("-")) {
                    String key = StringUtils.substringBefore(componentName, "-");
                    if (componentNameToServicePidMap.containsKey(key)) {
                        ComponentInfo componentInfo = componentNameToServicePidMap.get(key);
                        String pid = componentInfo.getServicePid();
                        if (pid.contains(key)) {
                            if (Component.STATE_ACTIVE != componentInfo.getState()) {
                                getResultLog().info("Component '{}' is in '{}' state, config checking skipping...", key,
                                        getComponentStateAsString(componentInfo.getState()));
                                result = true;
                            } else {
                                result = pid.length() > key.length();
                            }
                        }
                    }

                } else {
                    result = componentNameToServicePidMap.containsKey(componentName);
                }
            }

            return result;
        }

        private String getComponentStateAsString(int componentState) {
            String state;

            switch (componentState) {
                case Component.STATE_DISABLED: {
                    state = "DISABLED";
                    break;
                }
                case Component.STATE_UNSATISFIED: {
                    state = "UNSATISFIED";
                    break;
                }
                case Component.STATE_ACTIVE: {
                    state = "ACTIVE";
                    break;
                }
                case Component.STATE_REGISTERED: {
                    state = "REGISTERED";
                    break;
                }
                case Component.STATE_FACTORY: {
                    state = "FACTORY";
                    break;
                }
                case Component.STATE_DISPOSED: {
                    state = "DISPOSED";
                    break;
                }
                default: {
                    state = "UNKNOWN / NOT HANDLED";
                }
            }

            return state;
        }
    }

}
