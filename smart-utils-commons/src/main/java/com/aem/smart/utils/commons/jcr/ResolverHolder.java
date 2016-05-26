package com.aem.smart.utils.commons.jcr;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Resolver holder.
 */
public class ResolverHolder implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(ResolverHolder.class);

    private ResourceResolver resolver;

    /**
     * Instantiates a new Resolver holder.
     *
     * @param factory the factory
     */
    public ResolverHolder(final ResourceResolverFactory factory) {
        try {
            this.resolver = factory.getAdministrativeResourceResolver(null);

        } catch (LoginException ex) {
            LOG.error("Fail on getting resource resolver", ex);
        }
    }

    /**
     * Gets resolver.
     *
     * @return the resolver
     */
    public ResourceResolver getResolver() {
        return resolver;
    }

    /**
     * Gets session.
     *
     * @return the session
     */
    public Session getSession() {
        return resolver.adaptTo(Session.class);
    }

    public void close() {
        if ((resolver != null)) {
            resolver.close();
        }
    }
}