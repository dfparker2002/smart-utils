package com.aem.smart.utils.hc.configuration.util;

import com.aem.smart.utils.commons.jcr.ResolverHolder;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.hc.util.FormattingResultLog;
import org.apache.sling.query.SlingQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Custom logger to log replication agents configuration.
 */
@Component
@Service(ReplicationAgentsConfigurationsLogger.class)
public class ReplicationAgentsConfigurationsLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationAgentsConfigurationsLogger.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Activate
    public void activate(Map<String, Object> properties) {
        Objects.requireNonNull(resourceResolverFactory, "No reference to ResourceResolverFactory");
    }

    public void logProperties(FormattingResultLog resultLog) {
        try (ResolverHolder resolver = new ResolverHolder(resourceResolverFactory)) {

            for (String pathToCheck : getPathsToCheck()) {
                Resource resource = resolver.getResolver().getResource(pathToCheck);

                if (null == resource) {
                    LOGGER.warn("Path '{}' does not exist", pathToCheck);
                    continue;
                }

                for (String resourceTypeToSearch : getResourceTypesToSearch()) {
                    final SlingQuery query = SlingQuery.$(resource).find(resourceTypeToSearch);
                    for (Resource pathResource : query) {
                        resultLog.info(logProperties(pathResource));
                    }
                }
            }
        }
    }

    private String[] getPathsToCheck() {
        return new String[] { "/etc/replication/agents.author", "/etc/replication/agents.publish" };
    }

    private String[] getResourceTypesToSearch() {
        return new String[] { "cq/replication/components/agent" };
    }

    private Set<String> filterKeys(Set<String> keys) {
        return Sets.filter(keys, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return null != input &&
                        !input.startsWith("sling") &&
                        !input.startsWith("cq") &&
                        !input.startsWith("jcr");
            }
        });
    }

    private String logProperties(Resource pathResource) {
        String path = StringUtils.substringBeforeLast(pathResource.getPath(), "/");
        PropertiesLogger propertiesLogger = new PropertiesLogger(path);

        ValueMap valueMap = pathResource.adaptTo(ValueMap.class);
        Set<String> keys = filterKeys(valueMap.keySet());
        for (String key : keys) {
            String value = valueMap.get(key, String.class);
            propertiesLogger.add(key, value, "", true);
        }

        return propertiesLogger.log();
    }
}
