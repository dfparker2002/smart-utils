package com.aem.smart.utils.services.osgi.smartlogger;

import org.apache.felix.webconsole.DefaultVariableResolver;
import org.apache.felix.webconsole.SimpleWebConsolePlugin;
import org.apache.felix.webconsole.WebConsoleConstants;
import org.apache.felix.webconsole.WebConsoleUtil;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Andrii_Manuiev
 * Date: 21.05.13
 * Time: 14:38
 * Smart logger UI part
 */
public class SmartLogView extends SimpleWebConsolePlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartLogView.class);

    private static final String LABEL = "smartlogger";
    private static final String TITLE = "Smart logger";
    private static final String CSS[] = {"/" + LABEL + "/templates/smart-utils/css/smart-logger.css"};

    public static final String TEMPLATE_PATH = "/templates/smart-utils/smart-logger.html";

    private final String template;

    public SmartLogView() {
        super(LABEL, TITLE, CSS);
        template = readTemplateFile(TEMPLATE_PATH);
    }

    public static Dictionary getParams() {
        final Dictionary props = new Hashtable();
        props.put(Constants.SERVICE_DESCRIPTION, "Smart Utils Plugin for Apache Felix Console.");
        props.put(Constants.SERVICE_VENDOR, "EPAM Systems");
        props.put(WebConsoleConstants.PLUGIN_LABEL, LABEL);
        props.put(WebConsoleConstants.PLUGIN_TITLE, TITLE);
        props.put(WebConsoleConstants.PLUGIN_CSS_REFERENCES, "/" + LABEL + "/templates/smart-utils/css/smart-logger.css");
        return props;
    }

    @Override
    protected void renderContent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO: FIX IT - use bundle.properties instead (not load now for unknown reason)
        DefaultVariableResolver vars = ((DefaultVariableResolver) WebConsoleUtil.getVariableResolver(request));
        vars.put("smart.logger.list.title", "Select a log file");
        response.getWriter().print(template);
    }
}
