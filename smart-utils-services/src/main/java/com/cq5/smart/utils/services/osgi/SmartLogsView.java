package com.cq5.smart.utils.services.osgi;

import org.apache.commons.io.FileUtils;
import org.apache.felix.scr.annotations.*;
import org.apache.felix.webconsole.DefaultVariableResolver;
import org.apache.felix.webconsole.WebConsoleConstants;
import org.apache.felix.webconsole.WebConsoleUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Component
@Service(Servlet.class)
@Properties({
        @Property(name = WebConsoleConstants.PLUGIN_LABEL, value = "smartlogger"),
        @Property(name = WebConsoleConstants.PLUGIN_TITLE, value = "Smart Logger"),
        @Property(name = WebConsoleConstants.PLUGIN_CSS_REFERENCES, value = "/templates/smart-utils/css/smart-utils.css")
        })
public class SmartLogsView extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartLogsView.class);

    private static final String LOG_PARAM = "log";

    @Reference
    private SlingSettingsService slingSettings;

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

        DefaultVariableResolver resolver = (DefaultVariableResolver) WebConsoleUtil.getVariableResolver(req);
        resolver.put("logFileList", getFileList());

        renderContent(res);
    }

    private void renderContent(ServletResponse res) throws IOException {

        PrintWriter writer = res.getWriter();

        loadJSTemplate(writer);

        loadHTMLTemplate(writer);
    }

    private void loadJSTemplate(PrintWriter writer) {
        writer.println("<script type=\"text/javascript\">");
        writer.println(ResourceUtils.readTemplateFile(getClass(), "/templates/smart-utils/js/smart-utils.js"));
        writer.println("</script>");
    }

    private void loadHTMLTemplate(PrintWriter writer) {
        writer.println(ResourceUtils.readTemplateFile(getClass(), "/templates/smart-utils/smart-utils.html"));
    }

    private List<String> getFileList() {
        List<String> result = new LinkedList<String>();

        File logsFolder = new File(slingSettings.getAbsolutePathWithinSlingHome("logs"));

        final Collection<File> listOfLogs = FileUtils.listFiles(logsFolder, new String[]{"log"}, false);

        for (File logFile : listOfLogs) {
            result.add(String.format("'%s'", logFile.getName()));
        }
        return result;
    }

}
