package com.cq5.smart.utils.services.osgi.common;

import java.io.PrintWriter;

/**
 * Author: Andrii_Manuiev
 */
public interface LogFilter {

    public void apply(PrintWriter writer, String line);
}
