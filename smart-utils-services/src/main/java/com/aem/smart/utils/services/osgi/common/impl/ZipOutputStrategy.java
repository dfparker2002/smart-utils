package com.aem.smart.utils.services.osgi.common.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletResponse;

import com.aem.smart.utils.services.osgi.common.LogFilter;
import com.aem.smart.utils.services.osgi.common.OutputStrategy;

/**
 * The type Zip output strategy.
 */
public class ZipOutputStrategy implements OutputStrategy {

    @Override
    public void execute(SlingHttpServletResponse response, File logFile, LogFilter filter) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy-h:mm:ss");
        String formattedDate = sdf.format(Calendar.getInstance().getTime());

        String fileName = logFile.getName().replace(".log", StringUtils.EMPTY) + "_" + formattedDate;

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "inline; filename=" + fileName + ".zip;");

        ServletOutputStream outputStream = response.getOutputStream();

        try (ZipOutputStream zip = new ZipOutputStream(outputStream)) {
            try (BufferedReader fis = new BufferedReader(new FileReader(logFile))) {
                zip.putNextEntry(new ZipEntry(fileName + ".log"));

                while (true) {
                    String line = fis.readLine();
                    if (line != null) {
                        filter.apply(zip, line);
                    } else {
                        break;
                    }
                }
            }
            zip.flush();
        }
        outputStream.flush();
    }
}
