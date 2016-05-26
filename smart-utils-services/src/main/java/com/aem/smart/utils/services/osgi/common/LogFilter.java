package com.aem.smart.utils.services.osgi.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.DeflaterOutputStream;

/**
 * Author: Andrii_Manuiev
 */
public interface LogFilter {

    public static final String ENCODING = "UTF-8";

    public void apply(PrintWriter writer, String line);

    public void apply(DeflaterOutputStream stream, String line) throws IOException;
}
