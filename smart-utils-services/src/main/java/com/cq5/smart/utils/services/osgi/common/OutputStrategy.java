package com.cq5.smart.utils.services.osgi.common;

import org.apache.sling.api.SlingHttpServletResponse;

import java.io.File;
import java.io.IOException;

/**
 * Author: Andrii_Manuiev
 */
public interface OutputStrategy {

    static final String CONTENT_TYPE_PLAIN = "text/plain";
    static final String CONTENT_TYPE_ZIP = "application/zip";

    public void execute(SlingHttpServletResponse response, File logFile, LogFilter filter) throws IOException;
}
