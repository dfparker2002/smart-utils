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

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Andrii_Manuiev
 * Date: 20.05.13
 * Time: 14:53
 * This service is output a log file into browser.
 */
@SlingServlet(metatype = false, paths = SmartLogsReader.MAPPING_PATH)
public class SmartLogsReader extends SlingAllMethodsServlet {

    public static final java.lang.String MAPPING_PATH = "/services/smart-utils/logger";

    private static final String CONTENT_TYPE = "text/plain";
    private static final String PAGE_ENCODING = "UTF-8";

    public static final String LOG_FILE_PARAM = "file";
    public static final String LOG_FILTER_PARAM = "filter";

    @Reference
    private SlingSettingsService slingSettings;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(PAGE_ENCODING);

        String requestedFile = request.getParameter(LOG_FILE_PARAM);

        String requestedFilter = request.getParameter(LOG_FILTER_PARAM);

        FileWriterDecorator fileWriter = new FileWriterDecorator(response.getWriter());

        File logFile = new File(slingSettings.getAbsolutePathWithinSlingHome("logs").concat("/").concat(requestedFile));

        LogFilter filter = StringUtils.isNotBlank(requestedFilter) ? new LogFilterImpl(requestedFilter) : new EmptyLogFilter();

        if (logFile.exists()) {
            fileWriter.printFile(logFile, filter);
        } else {
            fileWriter.print("Wrong path to file");
        }

    }
}
