package com.aem.smart.utils.hc.configuration.util;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple holder for service configurations. Stores service pid and a map of properties.
 */
//TODO: rename
public final class FglConfiguration {

    private final String pid;
    private final Map<String, Object> properties;

    public FglConfiguration(String pid) {
        this.pid = pid;
        this.properties = new HashMap<>();
    }

    public void storeProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    public Map<String, Object> getProperties() {
        return new HashMap<>(properties);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pid", pid)
                .append("properties", properties)
                .toString();
    }

}
