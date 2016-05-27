package com.aem.smart.utils.hc;

import com.aem.smart.utils.hc.api.SearchAndPromoteConfiguration;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.hc.annotations.SlingHealthCheck;

import java.util.Map;
import java.util.Objects;

/**
 *
 */
@SlingHealthCheck(
        name = "S&P Connection Health Check",
        label = "S&P Connection Health Check",
        description = "Checks connectivity to S&P",
        tags = { "connectivity", "search-promote" }
)
public class SearchAndPromoteConnectionHealthCheck extends AbstractConnectivityHealthCheck {

    @Reference
    private SearchAndPromoteConfiguration searchAndPromoteConfiguration;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);

        Objects.requireNonNull(searchAndPromoteConfiguration, "No reference to SearchAndPromoteConfiguration");
    }

    @Override
    public String getUrlToCheck() {
        return searchAndPromoteConfiguration.getServerUrl() + "/?format=xml&count=15&searchByTerm=true&q=*";
    }

    @Override
    public String getHealthCheckName() {
        return "S&P";
    }
}
