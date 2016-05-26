package com.aem.smart.utils.hc.content;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.hc.util.FormattingResultLog;

/**
 * Simple context interface that provides handy methods for consistency checking.
 */
interface ConsistencyCheckContext {

    /**
     * Get a named property and convert it into the given type.
     *
     * @param propertyName The name of the property
     * @param type The class of the type
     * @return Return named value converted to type T or <code>null</code> if
     *         non existing or can't be converted.
     */
    <T> T getProperty(String propertyName, Class<T> type);

    String getStringProperty(String propertyName);

    String[] getStringArrayProperty(String propertyName);

    /**
     * Checks if specified property exists.
     *
     * @param propertyName name of the property to check
     */
    boolean propertyExists(String propertyName);

    /**
     * Check if property is a single-value or multiple-value.
     *
     * @param propertyName name of the property to check
     * @return true if property exists and is multi-value; false otherwise.
     */
    boolean isMultiValueProperty(String propertyName);

    /**
     * Return path of current resource.
     */
    String getResourcePath();

    /**
     * Returns an instance of {@link org.apache.sling.hc.util.FormattingResultLog}
     */
    FormattingResultLog getResultLog();

    /**
     * Resolves specifies path, using resource resolver of current resource under consistency check.
     *
     * @param path The absolute path to be resolved to a resource. If this
     *            parameter is <code>null</code>, it is assumed to address the
     *            root of the resource tree. If the path is relative it is
     *            assumed relative to the root, that is a slash is prepended to
     *            the path before resolving it.
     * @return The {@link org.apache.sling.api.resource.Resource} addressed by the <code>absPath</code> or a
     *          {@link org.apache.sling.api.resource.NonExistingResource} if no such resource can be resolved.
     */
    Resource resolve(String path);

}
