package com.aem.smart.utils.services.osgi.common.impl;

import com.aem.smart.utils.services.osgi.common.LogFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.zip.DeflaterOutputStream;

/**
 * Author: Andrii_Manuiev
 */
public class EmptyLogFilter implements LogFilter {

    @Override
    public void apply(PrintWriter writer, String line) {
        writer.println(line);
    }

    @Override
    public void apply(DeflaterOutputStream stream, String line) throws IOException {
        stream.write(line.getBytes(Charset.forName(ENCODING)));
    }
}
