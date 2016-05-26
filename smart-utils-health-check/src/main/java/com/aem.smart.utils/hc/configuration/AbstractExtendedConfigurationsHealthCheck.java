package com.aem.smart.utils.hc.configuration;

import com.aem.smart.utils.hc.AbstractRunmodeAwareHealthCheck;
import com.aem.smart.utils.hc.configuration.util.ActualConfigurationsLoader;
import com.aem.smart.utils.hc.configuration.util.ConfigurationLoader;
import com.aem.smart.utils.hc.configuration.util.FglConfiguration;
import com.aem.smart.utils.hc.configuration.util.PropertiesLogger;
import com.aem.smart.utils.hc.configuration.util.RunmodesConfigurationLoader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Component(componentAbstract = true)
abstract class AbstractExtendedConfigurationsHealthCheck extends AbstractRunmodeAwareHealthCheck {

    @Reference
    private RunmodesConfigurationLoader runmodesConfigurationLoader;

    @Reference
    private ActualConfigurationsLoader actualConfigurationsLoader;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);
        Objects.requireNonNull(runmodesConfigurationLoader, "No reference to RunmodesConfigurationLoader");
        Objects.requireNonNull(actualConfigurationsLoader, "No reference to ActualConfigurationsLoader");
    }

    protected final ConfigurationLoader getRunmodeConfigurationsLoader() {
        return runmodesConfigurationLoader;
    }

    protected final ConfigurationLoader getActualConfigurationsLoader() {
        return actualConfigurationsLoader;
    }

    protected final void checkMatches(Map<String, Object> properties, Map<String, Collection<Object>> valuesMapToCheck,
            PropertiesLogger propertiesLogger) {

        for (String key : properties.keySet()) {
            Object valueObj = properties.get(key);
            Collection<Object> valuesToCheck = valuesMapToCheck.get(key);
            if (null == valuesToCheck) {
                continue;
            }
            if (valuesToCheck.size() == 1) {
                Object valueToCheckObj = valuesToCheck.iterator().next();
                boolean equal = checkEquality(valueObj, valueToCheckObj);
                propertiesLogger.add(key, stringifyIfArray(valueObj), stringifyIfArray(valueToCheckObj), equal);
            } else {
                getLogger().warn("\n\t'{}' does not match '{}'", valueObj, valuesToCheck);
            }
        }
    }

    private boolean checkEquality(Object valueObj, Object valueToCheckObj) {
        boolean equal;
        if ((valueObj instanceof String[]) && (valueToCheckObj instanceof String[])) {
            equal = Arrays.equals((String[]) valueObj, (String[]) valueToCheckObj);
        } else {
            equal = Objects.equals(valueObj, valueToCheckObj);
        }
        return equal;
    }

    private Object stringifyIfArray(Object valueObj) {
        Object result = valueObj;
        if (valueObj instanceof String[]) {
            result = Arrays.asList((String[]) valueObj).toString();
        }
        return result;
    }

    protected static Multimap<String, Object> toMultimap(Collection<FglConfiguration> source) {
        Multimap<String, Object> multimap = HashMultimap.create();
        for (FglConfiguration configuration : source) {
            Map<String, Object> properties = configuration.getProperties();
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                multimap.put(entry.getKey(), entry.getValue());
            }
        }
        return multimap;
    }
}
