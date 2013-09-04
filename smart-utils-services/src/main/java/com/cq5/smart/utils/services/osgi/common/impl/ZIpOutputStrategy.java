package com.cq5.smart.utils.services.osgi.common.impl;

import com.cq5.smart.utils.services.osgi.common.LogFilter;
import com.cq5.smart.utils.services.osgi.common.OutputStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletResponse;

import javax.servlet.ServletOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Author: Andrii_Manuiev
 */
public class ZipOutputStrategy implements OutputStrategy {
    @Override
    public void execute(SlingHttpServletResponse response, File logFile, LogFilter filter) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy-h:mm:ss");
        String formattedDate = sdf.format(Calendar.getInstance().getTime());

        String fileName = logFile.getName().replace(".log", StringUtils.EMPTY) + "_" + formattedDate;

        response.setContentType(CONTENT_TYPE_ZIP);
        response.setHeader("Content-Disposition","inline; filename=" + fileName + ".zip;");

        ServletOutputStream outputStream = response.getOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        BufferedReader fis = new BufferedReader(new FileReader(logFile));
        zip.putNextEntry(new ZipEntry(fileName + ".log"));
        try {
            while (true) {
                String line = fis.readLine();
                if (line != null) {
                    filter.apply(zip, line);
                } else {
                    break;
                }
            }
        } finally {
            fis.close();
            zip.flush();
            zip.close();
            outputStream.flush();
        }
    }
}
