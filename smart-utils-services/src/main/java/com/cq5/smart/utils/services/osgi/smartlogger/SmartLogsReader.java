package com.cq5.smart.utils.services.osgi.smartlogger;

import com.cq5.smart.utils.services.osgi.common.FileWriterDecorator;
import com.cq5.smart.utils.services.osgi.common.LogFilter;
import com.cq5.smart.utils.services.osgi.common.impl.EmptyLogFilter;
import com.cq5.smart.utils.services.osgi.common.impl.LogFilterImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Andrii_Manuiev
 * Date: 20.05.13
 * Time: 14:53
 * This service is output a log file into browser or zip file.
 */
@SlingServlet(metatype = false, paths = SmartLogsReader.MAPPING_PATH)
public class SmartLogsReader extends SlingAllMethodsServlet {

    public static final java.lang.String MAPPING_PATH = "/services/smart-utils/logger";

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartLogsReader.class);

    private static final String CONTENT_TYPE_PLAIN = "text/plain";
    private static final String CONTENT_TYPE_ZIP = "application/zip";

    private static final String PAGE_ENCODING = "UTF-8";

    private static final String LOG_FILE_PARAM = "file";
    private static final String LOG_FILTER_PARAM = "filter";
    private static final String LOG_ACTION_PARAM = "action";


    @Reference
    private SlingSettingsService slingSettings;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding(PAGE_ENCODING);

        String requestedFile = request.getParameter(LOG_FILE_PARAM);
        String requestedFilter = request.getParameter(LOG_FILTER_PARAM);

        String requestedAction = request.getParameter(LOG_ACTION_PARAM);

        File logFile = new File(slingSettings.getAbsolutePathWithinSlingHome("logs").concat(File.separator).concat(requestedFile));

        if (logFile.exists()) {
            if (requestedAction.equals("zip")) {
                sendZip(response, logFile);
            } else {
                outputStream(response, logFile,  requestedFilter);
            }
        } else {
            LOGGER.error("Wrong file name, no log found: {}", logFile.getAbsolutePath());
        }
    }

    private void outputStream(SlingHttpServletResponse response, File logFile, String requestedFilter) throws IOException {
        response.setContentType(CONTENT_TYPE_PLAIN);

        FileWriterDecorator fileWriter = new FileWriterDecorator(response.getWriter());
        LogFilter filter = StringUtils.isNotBlank(requestedFilter) ? new LogFilterImpl(requestedFilter) : new EmptyLogFilter();
        fileWriter.printFile(logFile, filter);
    }

    private void sendZip(SlingHttpServletResponse response, File logFile) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy-h:mm:ss");
        String formattedDate = sdf.format(Calendar.getInstance().getTime());

        String fileName = logFile.getName().replace(".log", StringUtils.EMPTY) + "_" + formattedDate;

        response.setContentType(CONTENT_TYPE_ZIP);
        response.setHeader("Content-Disposition","inline; filename=" + fileName + ".zip;");

        ServletOutputStream outputStream = response.getOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        FileInputStream fis = new FileInputStream(logFile);
        try {
            zip.putNextEntry(new ZipEntry(fileName));
            byte[] b = new byte[1024];
            int len;

            while ((len = fis.read(b)) != -1) {
                zip.write(b, 0, len);
            }
        } finally {
            fis.close();
            zip.flush();
            zip.close();
            outputStream.flush();
        }

    }

}
