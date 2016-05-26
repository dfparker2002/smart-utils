package com.aem.smart.utils.commons.jcr;

import com.google.common.collect.Lists;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.List;

/**
 * The type Jcr property.
 */
public final class JcrProperty {

    /**
     * Is multi value boolean.
     *
     * @param node         the node
     * @param propertyName the property name
     * @return the boolean
     * @throws RepositoryException the repository exception
     */
    public static boolean isMultiValue(final Node node, final String propertyName) throws RepositoryException {
        return node.hasProperty(propertyName) && node.getProperty(propertyName).isMultiple();
    }

    /**
     * Remove property.
     *
     * @param node         the node
     * @param propertyName the property name
     * @throws RepositoryException the repository exception
     */
    public static void removeProperty(final Node node, final String propertyName) throws RepositoryException {
        if (node.hasProperty(propertyName)) {
            node.getProperty(propertyName).remove();
        }
    }

    /**
     * Get String property (Multiple or Single) as list
     * @param property source property
     * @return Array of strings
     *
     * @throws RepositoryException
     */
    public static List<String> getAsList(final Property property) throws RepositoryException {
        final List<String> result = Lists.newArrayList();
        if (property != null) {
            if (property.isMultiple()) {
                for (final Value val : property.getValues()) {
                    result.add(val.getString());
                }
            } else {
                result.add(property.getString());
            }
        }
        return result;
    }

    private JcrProperty() {
    }

}
