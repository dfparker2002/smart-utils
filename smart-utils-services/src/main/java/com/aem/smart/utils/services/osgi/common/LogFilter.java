package com.aem.smart.utils.services.osgi.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.DeflaterOutputStream;

/**
 * The interface Log filter.
 */
public interface LogFilter {

    /**
     * The constant ENCODING.
     */
    String ENCODING = "UTF-8";

    /**
     * Apply.
     *
     * @param writer the writer
     * @param line   the line
     */
    void apply(PrintWriter writer, String line);

    /**
     * Apply.
     *
     * @param stream the stream
     * @param line   the line
     * @throws IOException the io exception
     */
    void apply(DeflaterOutputStream stream, String line) throws IOException;
}
