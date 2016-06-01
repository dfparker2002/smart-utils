package com.aem.smart.utils.commons.jcr;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Session holder.
 */
public class SessionHolder implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SessionHolder.class);

    private static final String DEFAULT_USER = "admin";
    private static final SimpleCredentials CREDENTIALS = new SimpleCredentials(DEFAULT_USER,
            DEFAULT_USER.toCharArray());

    private Session session;

    /**
     * Create new SessionHolder instance from existence session
     *
     * @param currentSession current session
     */
    public SessionHolder(final Session currentSession) {
        this.session = currentSession;
    }

    /**
     * Create new SessionHolder instance
     *
     * @param repository repository for session resolver
     */
    public SessionHolder(final Repository repository) {
        try {
            this.session = repository.login(CREDENTIALS);
        } catch (RepositoryException e) {
            LOG.error("Can not create session in holder, authentication error: ", e);
        }
    }

    /**
     * Instantiates a new Session holder.
     *
     * @param request for open session
     */
    public SessionHolder(final SlingHttpServletRequest request) {
        this.session = request.getResourceResolver().adaptTo(Session.class);
    }

    /**
     * Gets session.
     *
     * @return Session session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Current session logout
     */
    @Override
    public void close() {
        if ((session != null) && (session.isLive())) {
            session.logout();
        }
    }
}
