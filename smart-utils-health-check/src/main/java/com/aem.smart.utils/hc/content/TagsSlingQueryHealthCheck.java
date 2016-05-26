package com.aem.smart.utils.hc.content;

import com.aem.smart.utils.commons.api.WebsiteConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 *
 */
@SlingHealthCheck(
        name = "Tags Health Check (with SlingQuery)",
        label = "Tags Health Check (with SlingQuery)",
        description = "Check if tags are present (with SlingQuery)",
        tags = {"atmosphere", "consistency"}
)
public class TagsSlingQueryHealthCheck extends AbstractContentConsistencyHealthCheck {

    @Property(
            cardinality = Integer.MAX_VALUE,
            label = "Tags path to check"
    )
    private static final String PATHS_TO_CHECK_PROPERTY = "paths.to.check";

    @Reference
    private WebsiteConfiguration websiteConfiguration;

    private Collection<String> pathsToCheck = Collections.emptyList();

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);
        Collection<String> paths = Arrays.asList(PropertiesUtil.toStringArray(properties.get(PATHS_TO_CHECK_PROPERTY)));
        if (paths.isEmpty()) {
            this.pathsToCheck = Collections.singletonList(websiteConfiguration.getTagsPath());
        } else {
            this.pathsToCheck = paths;
        }
        getLogger().debug("activate() {}, properties={}", this, properties);
    }

    @Override
    public Collection<String> getPathsToCheck() {
        return pathsToCheck;
    }

    @Override
    public Collection<String> getResourceTypesToSearch() {
        return Collections.singletonList("cq/tagging/components/tag");
    }

    @Override
    public String getLogToken() {
        return "tag";
    }

    @Override
    public void checkConsistency(ConsistencyCheckContext context) {
        //
    }

}
