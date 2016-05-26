package com.aem.smart.utils.hc.content;

import com.aem.smart.utils.commons.jcr.ResolverHolder;
import com.aem.smart.utils.hc.AbstractRunmodeAwareHealthCheck;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;

import java.util.Map;
import java.util.Objects;

/**
 *
 */
@SlingHealthCheck(
        name = "DAM Consistency Health Check (with SlingQuery)",
        label = "DAM Consistency Health Check (with SlingQuery)",
        description = "DAM Consistency Health Check (with SlingQuery)",
        tags = { "cq", "dam", "consistency" }
)
public class DamConsistencyHealthCheck extends AbstractRunmodeAwareHealthCheck {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);

        Objects.requireNonNull(resourceResolverFactory, "No reference to ResourceResolverFactory");
    }

    @Override
    protected void execute(FormattingResultLog resultLog) {

        try (ResolverHolder resolver = new ResolverHolder(resourceResolverFactory)) {

            String categoryPath = "/content/dam";
            Resource damResource = resolver.getResolver().getResource(categoryPath);

            if (null == damResource) {
                resultLog.warn("Path '{}' does not exist", categoryPath);

            } else {
                ValueMap valueMap = damResource.adaptTo(ValueMap.class);
                String scene7ConfigPath = valueMap.get("dam:scene7CloudConfigPath", String.class);
                if (StringUtils.isBlank(scene7ConfigPath)) {
                    resultLog.warn("No Scene7 configuration is found in property 'dam:scene7CloudConfigPath' under '/content/dam'");
                } else {
                    if (damResource.getResourceResolver().resolve(scene7ConfigPath) instanceof NonExistingResource) {
                        resultLog.warn("Scene7 configuration path '{}' does not exist or is not valid", scene7ConfigPath);
                    } else {
                        resultLog.debug("Checked scene7 config path '{}'", scene7ConfigPath);
                    }
                }
            }
        }
    }
}
