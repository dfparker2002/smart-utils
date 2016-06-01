package com.aem.smart.utils.services.osgi.common;

import org.apache.sling.api.SlingHttpServletResponse;

import java.io.File;
import java.io.IOException;

/**
 * The interface Output strategy.
 */
public interface OutputStrategy {

    /**
     * The constant CONTENT_TYPE_PLAIN.
     */
    String CONTENT_TYPE_PLAIN = "text/plain";

    /**
     * Execute.
     *
     * @param response the response
     * @param logFile  the log file
     * @param filter   the filter
     * @throws IOException the io exception
     */
    void execute(SlingHttpServletResponse response, File logFile, LogFilter filter) throws IOException;
}
