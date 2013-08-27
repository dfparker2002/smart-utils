package com.cq5.smart.utils.services.osgi.common.impl;

import com.cq5.smart.utils.services.osgi.common.LogFilter;

import java.io.PrintWriter;

/**
 * Author: Andrii_Manuiev
 */
public class EmptyLogFilter implements LogFilter {

    @Override
    public void apply(PrintWriter writer, String line) {
        writer.println(line);
    }
}
