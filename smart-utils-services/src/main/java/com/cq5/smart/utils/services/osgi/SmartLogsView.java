package com.cq5.smart.utils.services.osgi;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.webconsole.WebConsoleConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@Service(Servlet.class)
@Properties({
        @Property(name = WebConsoleConstants.PLUGIN_LABEL, value = "smartlogger"),
        @Property(name = WebConsoleConstants.PLUGIN_TITLE, value = "Smart Logger"),
        @Property(name = WebConsoleConstants.PLUGIN_CSS_REFERENCES, value = "/resources/templates/smart-utils/css/smart-utils.css")
})
public class SmartLogsView extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartLogsView.class);

    private static final String LOG_PARAM = "log";

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        renderContent(res);
    }

    private void renderContent(ServletResponse res) throws IOException {
        PrintWriter writer = res.getWriter();
        String template = ResourceUtils.readTemplateFile(getClass(), "/templates/smart-utils/smart-utils.html");
        writer.println(template);
    }

}
