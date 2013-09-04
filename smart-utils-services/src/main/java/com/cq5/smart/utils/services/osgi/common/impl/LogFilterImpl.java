package com.cq5.smart.utils.services.osgi.common.impl;

import com.cq5.smart.utils.services.osgi.common.LogFilter;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.zip.DeflaterOutputStream;

/**
 * Author: Andrii_Manuiev
 */
public class LogFilterImpl implements LogFilter {

    public static final String NEW_LINE = "\n";
    private String filterExpression;

    public LogFilterImpl(String requestedFilter) {
        filterExpression = requestedFilter;
    }

    public void apply(PrintWriter writer, String line) {
        if (StringUtils.isNotBlank(line) && line.contains(filterExpression)) {
            writer.println(line);
        }
    }

    @Override
    public void apply(DeflaterOutputStream stream, String line) throws IOException {
        if (StringUtils.isNotBlank(line) && line.contains(filterExpression)) {
            //FYI BufferedReader.readLine() eat a new line symbol, so need to add it
            stream.write(line.concat(NEW_LINE).getBytes(Charset.forName(ENCODING)));
        }
    }
}
