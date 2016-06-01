package com.aem.smart.utils.services.osgi.smartlogger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;

import org.apache.commons.io.FileUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.TidyJSONWriter;

/**
 * The type Smart log.
 * Provide information about log files for plugin.
 */
@SlingServlet(paths = "/services/smart-utils/smartlogs")
public class SmartLog extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartLog.class);

    private static final String CONTENT_TYPE = "application/json";
    private static final String PAGE_ENCODING = "UTF-8";

    @Reference
    private SlingSettingsService slingSettings;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(PAGE_ENCODING);

        List<String> fileList = getFileList();

        TidyJSONWriter jsonWriter = new TidyJSONWriter(response.getWriter());

        try {
            jsonWriter.object().key("fileList").array();

            for (String fileName : fileList) {
                jsonWriter.value(fileName);
            }

            jsonWriter.endArray().endObject();
        } catch (JSONException e) {
            LOGGER.error("Error write a JSON answer into output", e);
        }
    }

    private List<String> getFileList() {

        List<String> result = new LinkedList<>();

        String path = slingSettings.getAbsolutePathWithinSlingHome("logs");
        File logsFolder = new File(path);
        final Collection<File> listOfLogs = FileUtils.listFiles(logsFolder, new String[] { "log" }, true);

        result.addAll(
                listOfLogs.stream()
                        .map(logFile -> String.format("%s",
                                logFile.getAbsolutePath().replace(path + File.separator, "")))
                        .collect(Collectors.toList()));
        return result;
    }
}
