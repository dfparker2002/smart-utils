package com.aem.smart.utils.hc.configuration;

import com.aem.smart.utils.hc.AbstractRunmodeAwareHealthCheck;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.util.FormattingResultLog;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.util.Dictionary;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract class that contains some common code for configuration health checks.
 */
@Component(componentAbstract = true)
abstract class AbstractConfigurationHealthCheck extends AbstractRunmodeAwareHealthCheck {

    @Reference
    private ConfigurationAdmin configurationAdmin;

    /**
     * Return PID of the service to be health checked.
     *
     * @return the service pid
     */
    public abstract String getServicePid();

    /**
     * Perform necessary configuration checks in this method.
     *
     * @param context - context that stores necessary information about current service configuration parameters.
     */
    public abstract void checkConfiguration(ConfigurationCheckContext context);

    /**
     * Return simple message to be used in {@link org.apache.sling.hc.util.FormattingResultLog} log
     *
     * @return the health check token
     */
    public abstract String getHealthCheckToken();


    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);
        Objects.requireNonNull(configurationAdmin, "No reference to ConfigurationAdmin");
    }

    protected void execute(String siteName, FormattingResultLog resultLog) {

        try {
            Configuration configuration = configurationAdmin.getConfiguration(getServicePid());
            Dictionary properties = configuration.getProperties();
            checkConfiguration(new DefaultConfigurationCheckContext(properties, resultLog));

        } catch (Exception ex) {
            getLogger().error("Failed to check " + getHealthCheckToken() + " config: ", ex);
            resultLog.warn("Failed to check {} config: '{}'", getHealthCheckToken(), ex);
        }
    }


    /**
     * Simple context interface that provides handy methods for configuration checking.
     */
    interface ConfigurationCheckContext {

        /**
         * Return property value as string.
         *
         * @param propertyName the property name
         * @return the string property
         */
        String getStringProperty(String propertyName);

        /**
         * Return property value as array of Strings.
         *
         * @param propertyName the property name
         * @return the string [ ]
         */
        String[] getStringArrayProperty(String propertyName);

        /**
         * Return property value as boolean.
         *
         * @param propertyName the property name
         * @return the boolean property
         */
        boolean getBooleanProperty(String propertyName);

        /**
         * Return property value as int.
         *
         * @param propertyName the property name
         * @return the integer property
         */
        int getIntegerProperty(String propertyName);

        /**
         * Returns an instance of {@link org.apache.sling.hc.util.FormattingResultLog} to be used in
         * {@link AbstractConfigurationHealthCheck#checkConfiguration(com.aem.smart.utils.hc.configuration.AbstractConfigurationHealthCheck.ConfigurationCheckContext)}
         *
         * @return the result log
         */
        FormattingResultLog getResultLog();
    }

    /**
     * Default simple implementation of {@link com.aem.smart.utils.hc.configuration.AbstractConfigurationHealthCheck.ConfigurationCheckContext}
     */
    static class DefaultConfigurationCheckContext implements ConfigurationCheckContext {

        private final Dictionary properties;
        private final FormattingResultLog resultLog;

        /**
         * Instantiates a new Default configuration check context.
         *
         * @param properties the properties
         * @param resultLog  the result log
         */
        public DefaultConfigurationCheckContext(Dictionary properties, FormattingResultLog resultLog) {
            this.properties = properties;
            this.resultLog = resultLog;
        }

        public String getStringProperty(String propertyName) {
            return PropertiesUtil.toString(properties.get(propertyName), "");
        }

        public String[] getStringArrayProperty(String propertyName) {
            return PropertiesUtil.toStringArray(properties.get(propertyName), new String[0]);
        }

        public boolean getBooleanProperty(String propertyName) {
            return PropertiesUtil.toBoolean(properties.get(propertyName), Boolean.FALSE);
        }

        public int getIntegerProperty(String propertyName) {
            return PropertiesUtil.toInteger(properties.get(propertyName), 0);
        }

        public FormattingResultLog getResultLog() {
            return resultLog;
        }
    }
}
