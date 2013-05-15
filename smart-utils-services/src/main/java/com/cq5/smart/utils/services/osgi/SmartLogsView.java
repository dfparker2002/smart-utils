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

//TODO: smart logger should ask a service fol log file by name and output it to browser
//TODO: smart logger should have ability to archive a selected log file and send it to user
//TODO: smart logger should have ability a output only line which contain a part of string
//TODO: smart logger should have ability to select string with part of string and archive it and give it to user
@Component
@Service(Servlet.class)
@Properties({
        @Property(name = WebConsoleConstants.PLUGIN_LABEL, value = "smartlogger"),
        @Property(name = WebConsoleConstants.PLUGIN_TITLE, value = "Smart Logger"),
        @Property(name = WebConsoleConstants.PLUGIN_CSS_REFERENCES, value = "/res/ui/smart_logger.css")
})
public class SmartLogsView extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartLogsView.class);

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        PrintWriter writer = res.getWriter();
        writer.print("This is a test plugin for Felix web console");
    }
}
