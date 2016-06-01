package com.aem.smart.utils.hc;

import java.util.Map;
import java.util.Objects;

import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;

import com.aem.smart.utils.hc.api.HybrisRestConnectionConfig;

@SlingHealthCheck(name = "Hybris Connection Health Check", label = "Hybris Connection Health Check", description = "Checks connectivity to hybris", tags = {
        "connectivity", "hybris" })
public class HybrisConnectionHealthCheck extends AbstractConnectivityHealthCheck {

    @Property(label = "Hybris endpoint to check connectivity to")
    private static final String HYBRIS_ENDPOINT_TO_CHECK = "hc.connection.hybris.url";

    @Reference
    private HybrisRestConnectionConfig hybrisRestConnectionConfig;

    private String hybrisEndpointToChek;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);

        Objects.requireNonNull(hybrisRestConnectionConfig, "No reference to HybrisRestConnectionConfig");

        final String endPoint = PropertiesUtil.toString(properties.get(HYBRIS_ENDPOINT_TO_CHECK), null);
        Objects.requireNonNull(endPoint, "Fail to check empty endpoint");

        this.hybrisEndpointToChek = endPoint;
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
