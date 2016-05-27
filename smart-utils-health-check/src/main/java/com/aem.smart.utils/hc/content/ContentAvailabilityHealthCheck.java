package com.aem.smart.utils.hc.content;

import com.aem.smart.utils.hc.AbstractRunmodeAwareHealthCheck;
import com.google.common.collect.Iterators;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
@SlingHealthCheck(
        name = "Content Availability Health Check",
        label = "Content Availability Health Check",
        description = "Checks the content availability.",
        tags = { "content", "availability" }
)
public class ContentAvailabilityHealthCheck extends AbstractRunmodeAwareHealthCheck {

    @Property(unbounded= PropertyUnbounded.ARRAY,
            label="Paths to Check",
            description="The list of paths to check, optionally with expected HTTP status responses. " +
                        "An entry like \"/tmp/test.txt:301\", for example, checks that /tmp/test.txt returns a " +
                        "301 response.")
    private static final String PROP_PATH = "path";

    @Reference
    private ResourceResolverFactory resolverFactory;

    private String [] paths;

    @Override
    public void activate(final Map<String, Object> properties) {
        super.activate(properties);

        paths = PropertiesUtil.toStringArray(properties.get(PROP_PATH), new String[]{});
        getLogger().info("Activated, paths=[{}]", Arrays.asList(paths));
    }

    @Override
    protected void execute(String siteName, FormattingResultLog resultLog) {

        ResourceResolver resolver = null;
        int checked = 0;
        int failed = 0;
        String lastPath = null;

        try {
            resolver = resolverFactory.getAdministrativeResourceResolver(null);
            for (String p : paths) {
                lastPath = p;

                Resource resource = resolver.resolve(p);
                if (null == resource) {
                    failed++;
                    resultLog.warn("Path '{}' seems to be invalid or does not exist", p);
                } else {
                    resultLog.debug("Path '{}' exists, children count is '{}'", p, Iterators.size(resource.getChildren().iterator()));
                }
                checked++;
            }
        } catch(Exception e) {
            resultLog.warn("Exception while executing request [{}]: {}", lastPath, e);

        } finally {
            if (null != resolver) {
                resolver.close();
            }
        }

        if (checked == 0) {
            resultLog.warn("No paths checked, empty paths list?");
        } else {
            resultLog.debug("{} paths checked, {} failures", checked, failed);
        }
    }
}
