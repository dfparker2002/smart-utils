package com.aem.smart.utils.hc.configuration.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.hc.util.FormattingResultLog;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

/**
 *
 */
final class RunmodesConfigurationLoaderHelper {
    //TODO: remove hardcoded
    private static final String RUNMODES_QUERY = "select * from [sling:OsgiConfig] as runmodeConfig "
            + "WHERE ISDESCENDANTNODE(runmodeConfig, '/apps/sportchek/runmodes/%s')";

    private final Multimap<String, FglConfiguration> fglConfigurationsMap = ArrayListMultimap.create();
    private final QueryManager queryManager;
    private final FormattingResultLog resultLog;

    public RunmodesConfigurationLoaderHelper(QueryManager queryManager, FormattingResultLog resultLog) {
        this.queryManager = queryManager;
        this.resultLog = resultLog;
    }

    public void loadConfigurationsForRunmode(String runmodeName, RunmodeConfigurationsFilter filter)
            throws RepositoryException {

        String queryStatement = String.format(RUNMODES_QUERY, runmodeName);
        Query query = queryManager.createQuery(queryStatement, Query.JCR_SQL2);
        NodeIterator nodeIterator = query.execute().getNodes();

        if (!nodeIterator.hasNext()) {
            resultLog.warn("No configurations has been found for '{}' runmode", runmodeName);
            return;
        }

        while (nodeIterator.hasNext()) {
            final Node runmodeConfigurationNode = nodeIterator.nextNode();
            String runmodeConfigurationName = runmodeConfigurationNode.getName();
            if (!runmodeConfigurationName.contains("LogManager")) {
                runmodeConfigurationName = StringUtils.substringBeforeLast(runmodeConfigurationName, "-");
                if (filter.apply(runmodeConfigurationName)) {
                    final PropertyIterator propertyIterator = runmodeConfigurationNode.getProperties();
                    FglConfiguration fglConfiguration = readRunmodeConfiguration(runmodeConfigurationName, propertyIterator);
                    if (fglConfigurationsMap.containsKey(runmodeConfigurationName)) {
                        resultLog.debug("Runmode configurations map already contains configuration for '{}', in '{}'",
                                runmodeConfigurationName, runmodeName);
                    } else {
                        fglConfigurationsMap.put(runmodeConfigurationName, fglConfiguration);
                    }
                }
            }
        }
    }

    public Multimap<String, FglConfiguration> getFglConfigurationsMap() {
        return ArrayListMultimap.create(fglConfigurationsMap);
    }

    private FglConfiguration readRunmodeConfiguration(String runmodeName, PropertyIterator propertyIterator)
            throws RepositoryException {

        FglConfiguration fglConfiguration = new FglConfiguration(runmodeName);
        while (propertyIterator.hasNext()) {
            Property property = propertyIterator.nextProperty();
            String propertyName = property.getName();
            if (!propertyName.startsWith("jcr")) {
                fglConfiguration.storeProperty(propertyName, readValue(property));
            }
        }
        return fglConfiguration;
    }

    private Object readValue(Property property) throws RepositoryException {
        Object result;
        if (property.isMultiple()) {
            Value[] values = property.getValues();
            String[] arr = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                arr[i] = String.valueOf(readValueAsObject(values[i]));
            }
            result = arr;
        } else {
            Value value = property.getValue();
            result = readValueAsObject(value);
        }
        return result;
    }

    private Object readValueAsObject(Value value) throws RepositoryException {
        Object result;

        switch (value.getType()) {
            case PropertyType.BINARY: {
                result = value.getBinary();
                break;
            }
            case PropertyType.BOOLEAN: {
                result = value.getBoolean();
                break;
            }
            case PropertyType.DATE: {
                result = value.getDate();
                break;
            }
            case PropertyType.DECIMAL: {
                result = value.getDecimal();
                break;
            }
            case PropertyType.DOUBLE: {
                result = value.getDouble();
                break;
            }
            case PropertyType.LONG: {
                result = value.getLong();
                break;
            }
            default: {
                result = value.getString();
            }
        }
        return result;
    }
}
