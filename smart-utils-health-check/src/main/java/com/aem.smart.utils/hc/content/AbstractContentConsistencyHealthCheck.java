package com.aem.smart.utils.hc.content;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.hc.util.FormattingResultLog;
import org.apache.sling.query.SlingQuery;

import com.aem.smart.utils.commons.jcr.ResolverHolder;
import com.aem.smart.utils.hc.AbstractRunmodeAwareHealthCheck;
import com.google.common.base.Joiner;

/**
 * Abstract class to be used by content consistency health checkers.
 */
@Component(componentAbstract = true)
abstract class AbstractContentConsistencyHealthCheck extends AbstractRunmodeAwareHealthCheck {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * Return an array of paths for consistency check.
     */
    public abstract Collection<String> getPathsToCheck();

    /**
     * Return an array of values of 'sling:resourceType' to be used in searching of particular nodes.
     */
    public abstract Collection<String> getResourceTypesToSearch();

    /**
     * Perform necessary consistency checks in this method.
     *
     * @param context - context that stores necessary information about current resource for consistency check.
     */
    public abstract void checkConsistency(ConsistencyCheckContext context);

    /**
     * Return simple message token to be used in {@link FormattingResultLog} log
     */
    public abstract String getLogToken();

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);
        Objects.requireNonNull(resourceResolverFactory, "No reference to ResourceResolverFactory");
    }

    @Override
    protected void execute(FormattingResultLog log) {
        try (ResolverHolder resolver = new ResolverHolder(resourceResolverFactory)) {

            for (String pathToCheck : getPathsToCheck()) {
                Resource resource = resolver.getResolver().getResource(pathToCheck);

                if (null == resource) {
                    log.warn("Path '{}' does not exist", pathToCheck);
                    continue;
                }

                boolean atLeastOneValidEntryFound = false;
                // TODO: ATMO: optimize properly
                for (String resourceTypeToSearch : getResourceTypesToSearch()) {
                    final SlingQuery query = SlingQuery.$(resource).find(resourceTypeToSearch);
                    int count = 0;
                    for (Resource pathResource : query) {
                        checkConsistency(new DefaultConsistencyCheckContext(pathResource, log));
                        ++count;
                    }
                    if (count > 0) {
                        log.info("Checked '{}' {}s in '{}'", count, getLogToken(), pathToCheck);
                        atLeastOneValidEntryFound = true;
                    } else {
                        log.debug("No {}s of type '{}' have been found in '{}'", getLogToken(), resourceTypeToSearch,
                                pathToCheck);
                    }
                }

                if (!atLeastOneValidEntryFound) {
                    log.warn("No {}s have been found in '{}'", getLogToken(), pathToCheck);
                }
            }
        }
    }

    @Override
    public String toString() {
        return getClass() + "{" + "pathsToChek=" + Joiner.on(',').join(getPathsToCheck()) + '}';
    }
}
