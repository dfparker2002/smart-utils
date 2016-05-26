package com.aem.smart.utils.hc.content;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.hc.util.FormattingResultLog;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Default simple implementation of {@link ConsistencyCheckContext}
 */
public class DefaultConsistencyCheckContext implements ConsistencyCheckContext {

    private final ValueMap valueMap;
    private final String resourcePath;
    private final FormattingResultLog resultLog;
    private final ResourceResolver resourceResolver;
    private final Node currentNode;

    public DefaultConsistencyCheckContext(Resource currentResource, FormattingResultLog resultLog) {
        this.valueMap = currentResource.adaptTo(ValueMap.class);
        this.resourcePath = currentResource.getPath();
        this.resultLog = resultLog;
        this.resourceResolver = currentResource.getResourceResolver();
        this.currentNode = currentResource.adaptTo(Node.class);
    }

    @Override
    public <T> T getProperty(String propertyName, Class<T> type) {
        return valueMap.get(propertyName, type);
    }

    @Override
    public String getStringProperty(String propertyName) {
        return getProperty(propertyName, String.class);
    }

    @Override
    public String[] getStringArrayProperty(String propertyName) {
        return getProperty(propertyName, String[].class);
    }

    @Override
    public boolean propertyExists(String propertyName) {
        return valueMap.containsKey(propertyName);
    }

    @Override
    public boolean isMultiValueProperty(String propertyName) {
        try {
            if (currentNode.hasProperty(propertyName)) {
                return currentNode.getProperty(propertyName).isMultiple();
            }

        } catch (RepositoryException e) {
            getResultLog().warn("Exception occurred during reading of node's property", e);
        }

        return false;
    }

    @Override
    public String getResourcePath() {
        return resourcePath;
    }

    @Override
    public FormattingResultLog getResultLog() {
        return resultLog;
    }

    @Override
    public Resource resolve(String path) {
        return resourceResolver.resolve(path);
    }

}
