package com.aem.smart.utils.hc.configuration.util;

import java.util.Arrays;

/**
 * Simple class to store and create json string from key/value properties and service pid.
 */
public final class PropertiesLogger {

    private final StringBuilder stringBuilder;
    private boolean first;

    public PropertiesLogger(String pid) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("{").append("\"name\":\"").append(pid).append("\",\"details\":[");
        first = true;
    }

    public void add(String key, Object value, Object runmodeValue, boolean conformsToRunmodes) {
        if (!first) {
            stringBuilder.append(",");
        }
        stringBuilder.append("{")
                .append("\"name\":\"").append(key)
                .append("\",\"value\":\"").append(value)
                .append("\",\"runmodeValue\":\"");
        if (runmodeValue instanceof String[]) {
            stringBuilder.append(Arrays.asList((String[]) runmodeValue));
        } else {
            stringBuilder.append(runmodeValue);
        }
        stringBuilder.append("\",\"equalsToRunmodeValue\":").append(conformsToRunmodes).append("}");
        first = false;
    }

    public String log() {
        stringBuilder.append("]}");
        return stringBuilder.toString();
    }
}
