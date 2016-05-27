package com.aem.smart.utils.hc;

import org.apache.commons.lang.ArrayUtils;
import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;
import org.osgi.framework.Constants;

import java.util.Map;
import java.util.Objects;

/**
 *
 */
@SlingHealthCheck(
        name = "OSGi Components Health Check",
        label = "OSGi Components Health Check",
        description = "Check if OSGi components matching 'filter.criteria' property are in 'unsatisfied' state",
        tags = { "cq", "osgi", "consistency" }
)
public class ComponentsHealthCheck extends AbstractRunmodeAwareHealthCheck {

    @Reference
    private ScrService scrService;

    @Property(
            cardinality = Integer.MAX_VALUE,
            value = {},
            label = "Part of service.pid to filter components"
    )
    private static final String SERVICE_PID_PARTS_PROPERTY = "filter.criteria";

    private String[] servicePidParts = ArrayUtils.EMPTY_STRING_ARRAY;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);

        Objects.requireNonNull(scrService, "No reference to ScrService");

        this.servicePidParts = PropertiesUtil.toStringArray(
                properties.get(SERVICE_PID_PARTS_PROPERTY),
                ArrayUtils.EMPTY_STRING_ARRAY
        );
    }

    @Override
    protected void execute(String siteName, FormattingResultLog resultLog) {

        Component[] components = scrService.getComponents();

        int count = 0;
        for (Component component : components) {
            String servicePid = String.valueOf(component.getProperties().get(Constants.SERVICE_PID));
            for (String servicePidPart : servicePidParts) {
                if (servicePid.contains(servicePidPart)) {
                    if (Component.STATE_UNSATISFIED == component.getState()) {
                        resultLog.warn("Component '{}' is in 'UNSATISFIED' state", servicePid, component.getState());
                    }
                    ++count;
                }
            }
        }
        resultLog.info("Checked '{}' components", count);
    }
}
