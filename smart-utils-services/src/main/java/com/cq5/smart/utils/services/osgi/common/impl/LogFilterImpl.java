package com.cq5.smart.utils.services.osgi.common.impl;

import com.cq5.smart.utils.services.osgi.common.LogFilter;
import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;

/**
 * Author: Andrii_Manuiev
 */
public class LogFilterImpl implements LogFilter {

    private String filterExpression;

    public LogFilterImpl(String requestedFilter) {
        filterExpression = requestedFilter;
    }

    public void apply(PrintWriter writer, String line) {
        if (StringUtils.isNotBlank(line) && line.contains(filterExpression)) {
            writer.println(line);
        }
    }
}
