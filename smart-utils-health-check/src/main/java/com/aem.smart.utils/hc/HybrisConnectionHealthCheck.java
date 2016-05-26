package com.aem.smart.utils.hc;

import com.aem.smart.utils.commons.api.HybrisRestConnectionConfig;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;

import java.util.Map;
import java.util.Objects;

@SlingHealthCheck(
        name = "FGL Hybris Connection Health Check",
        label = "FGL Hybris Connection Health Check",
        description = "Checks connectivity to hybris",
        tags = { "connectivity", "hybris" }
)
public class HybrisConnectionHealthCheck extends AbstractConnectivityHealthCheck {

    private static final String DEFAULT_HYBRIS_ENDPOINT_TO_CHECK = "/rest/v1/atmosphere/cart/mini";

    @Property(label = "Hybris endpoint to check connectivity to")
    private static final String HYBRIS_ENDPOINT_TO_CHECK = "hc.connection.hybris.url";

    @Reference
    private HybrisRestConnectionConfig hybrisRestConnectionConfig;

    private String hybrisEndpointToChek;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);

        Objects.requireNonNull(hybrisRestConnectionConfig, "No reference to HybrisRestConnectionConfig");

        this.hybrisEndpointToChek = PropertiesUtil.toString(
                properties.get(HYBRIS_ENDPOINT_TO_CHECK),
                DEFAULT_HYBRIS_ENDPOINT_TO_CHECK);
    }

    @Override
    public String getUrlToCheck() {
        return hybrisRestConnectionConfig.getBaseUrl() + hybrisEndpointToChek;
    }

    @Override
    public String getHealthCheckName() {
        return "Hybris";
    }
}
