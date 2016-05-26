package com.aem.smart.utils.commons.jcr;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.InvalidItemStateException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

public class SessionHolder implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SessionHolder.class);
    private static final String DEFAULT_USER = "admin";
    private static final SimpleCredentials CREDENTIALS = new SimpleCredentials(DEFAULT_USER,
            DEFAULT_USER.toCharArray());
    private Session session;

    /**
     * Create new SessionHolder instance from existence session
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
            if (repository instanceof SlingRepository) {
                this.session = ((SlingRepository) repository).loginAdministrative(null);
            } else {
                this.session = repository.login(CREDENTIALS);
            }
        } catch (RepositoryException e) {
            LOG.error("Can not create session in holder, authentication error: ", e);
        }
    }

    /**
     *
     * @param request for open session
     */
    public SessionHolder(final SlingHttpServletRequest request) {
        this.session = request.getResourceResolver().adaptTo(Session.class);
    }

    /**
     *
     * @return Session
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

    /**
     * Auto saving or auto refresh and saving of session after method execution
     * @param function - Callback function
     * @throws SessionHolderException Wrapped any exception
     * @throws RepositoryException Repository exception
     */
    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "unchecked" })
    public void sessionApply(final SessionCallable<Session> function)
            throws SessionHolderException, RepositoryException {
        try {
            function.call(session);
        } catch (Exception commonEx) {
            throw new SessionHolderException(commonEx);
        }
        try {
            session.save();
        } catch (InvalidItemStateException ex) {
            session.refresh(true);
            try {
                function.call(session);
            } catch (Exception commonEx) {
                throw new SessionHolderException(commonEx);
            }
            session.save();
        }
    }

    /**
     * Session method manipulation wrapper
     * @param <Session> Session for processing
     */
    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "unchecked" })
    public interface SessionCallable<Session> {
        void call(final Session session) throws Exception;
    }

    /**
     * The class is wrapper for the Exception
     */
    public static class SessionHolderException extends Exception {

        /**
         * Creates new instance from common Exception
         * @param commonEx Wrapped Exception
         */
        public SessionHolderException(Exception commonEx) {
            super(commonEx);
        }
    }
}
