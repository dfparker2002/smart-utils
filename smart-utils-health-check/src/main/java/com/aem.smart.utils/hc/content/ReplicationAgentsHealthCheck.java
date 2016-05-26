package com.aem.smart.utils.hc.content;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;

/**
 *
 */
@SlingHealthCheck(name = "Replication Agents Health Check (with SlingQuery)", label = "Replication Agents Health Check (with SlingQuery)", description = "Checks and reports enabled replication agents (with SlingQuery)", tags = {
        "atmosphere", "replication" })
public class ReplicationAgentsHealthCheck extends AbstractContentConsistencyHealthCheck {

    @Property(cardinality = Integer.MAX_VALUE, value = { "/etc/replication/agents.author",
            "/etc/replication/agents.publish" }, label = "Replication agents path to check")
    private static final String PATHS_TO_CHECK_PROPERTY = "paths.to.check";

    private Collection<String> pathsToCheck = Collections.emptyList();

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);
        this.pathsToCheck = Arrays.asList(
                PropertiesUtil.toStringArray(properties.get(PATHS_TO_CHECK_PROPERTY), ArrayUtils.EMPTY_STRING_ARRAY));
    }

    @Override
    public Collection<String> getPathsToCheck() {
        return pathsToCheck;
    }

    @Override
    public Collection<String> getResourceTypesToSearch() {
        return Collections.singletonList("cq/replication/components/agent");
    }

    @Override
    public void checkConsistency(ConsistencyCheckContext context) {
        String enabled = context.getProperty("enabled", String.class);
        if (Boolean.valueOf(enabled)) {
            String transportUri = context.getStringProperty("transportUri");
            context.getResultLog().debug("Found enabled agent '{}' with transportUri '{}'", context.getResourcePath(),
                    transportUri);
        }
    }

    @Override
    public String getLogToken() {
        return "replication agent";
    }
}
