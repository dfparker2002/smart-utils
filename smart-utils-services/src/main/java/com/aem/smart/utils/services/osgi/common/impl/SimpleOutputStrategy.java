package com.aem.smart.utils.services.osgi.common.impl;

import com.aem.smart.utils.services.osgi.common.FileWriterDecorator;
import com.aem.smart.utils.services.osgi.common.LogFilter;
import com.aem.smart.utils.services.osgi.common.OutputStrategy;
import org.apache.sling.api.SlingHttpServletResponse;

import java.io.File;
import java.io.IOException;

/**
 * Author: Andrii_Manuiev
 */
public class SimpleOutputStrategy implements OutputStrategy {

    @Override
    public void execute(SlingHttpServletResponse response, File logFile, LogFilter filter) throws IOException {
        response.setContentType(CONTENT_TYPE_PLAIN);
        FileWriterDecorator fileWriter = new FileWriterDecorator(response.getWriter());
        fileWriter.printFile(logFile, filter);
    }
}
