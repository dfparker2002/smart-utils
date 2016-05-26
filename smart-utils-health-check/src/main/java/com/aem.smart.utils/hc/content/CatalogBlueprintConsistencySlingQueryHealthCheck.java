package com.aem.smart.utils.hc.content;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;

import com.aem.smart.utils.commons.api.WebsiteConfiguration;
import com.aem.smart.utils.hc.RunmodeAwareHealthCheck;

/**
 *
 */
@SlingHealthCheck(name = "Catalog Blueprint Health Check (with SlingQuery)", label = "Catalog Blueprint Health Check (with SlingQuery)", description = "Checks presence of catalog blueprint (with SlingQuery)", tags = {
        "atmosphere", "consistency" })
@Properties({ @Property(name = RunmodeAwareHealthCheck.ENABLED_ON_RUNMODES_PROPERTY, value = {
        RunmodeAwareHealthCheck.RUNMODE_AUTHOR }, cardinality = Integer.MAX_VALUE, label = "Runmodes to run this healthchek on") })
public class CatalogBlueprintConsistencySlingQueryHealthCheck extends AbstractContentConsistencyHealthCheck {

    @Property(cardinality = Integer.MAX_VALUE, label = "Catalog Blueprint path to check")
    private static final String PATHS_TO_CHECK_PROPERTY = "paths.to.check";

    private Collection<String> pathsToCheck = Collections.emptyList();

    @Reference
    private WebsiteConfiguration websiteConfiguration;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);
        Collection<String> paths = Arrays.asList(PropertiesUtil.toStringArray(properties.get(PATHS_TO_CHECK_PROPERTY)));
        if (paths.isEmpty()) {
            this.pathsToCheck = Collections.singletonList(websiteConfiguration.getCatalogPath());
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
        return Collections.singletonList("sportchek/admin-pages/admin-catalog-section-page");
    }

    @Override
    public String getLogToken() {
        return "catalog blueprint";
    }

    @Override
    public void checkConsistency(ConsistencyCheckContext context) {
        //
    }

}
