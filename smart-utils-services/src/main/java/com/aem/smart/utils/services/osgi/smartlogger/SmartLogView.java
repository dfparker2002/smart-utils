package com.aem.smart.utils.services.osgi.smartlogger;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.DefaultVariableResolver;
import org.apache.felix.webconsole.SimpleWebConsolePlugin;
import org.apache.felix.webconsole.WebConsoleConstants;
import org.apache.felix.webconsole.WebConsoleUtil;
import org.osgi.framework.Constants;

/**
 * The type Smart log view.
 * Smart logger UI part
 */
public class SmartLogView extends SimpleWebConsolePlugin {

    private static final String LABEL = "smartlogger";
    private static final String TITLE = "Smart logger";
    private static final String CSS[] = { "/" + LABEL + "/templates/smart-utils/css/smart-logger.css" };

    private static final String TEMPLATE_PATH = "/templates/smart-utils/smart-logger.html";

    private final String template;

    /**
     * Instantiates a new Smart log view.
     */
    public SmartLogView() {
        super(LABEL, TITLE, CSS);
        template = readTemplateFile(TEMPLATE_PATH);
    }

    /**
     * Gets params.
     *
     * @return the params
     */
    public static Dictionary getParams() {
        final Dictionary<String, Object> props = new Hashtable();
        props.put(Constants.SERVICE_DESCRIPTION, "Smart Utils Plugin for Apache Felix Console.");
        props.put(Constants.SERVICE_VENDOR, "EPAM Systems");
        props.put(WebConsoleConstants.PLUGIN_LABEL, LABEL);
        props.put(WebConsoleConstants.PLUGIN_TITLE, TITLE);
        props.put(WebConsoleConstants.PLUGIN_CSS_REFERENCES,
                "/" + LABEL + "/templates/smart-utils/css/smart-logger.css");
        return props;
    }

    @Override
    protected void renderContent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: FIX IT - use bundle.properties instead (not load now for unknown reason)
        DefaultVariableResolver vars = ((DefaultVariableResolver) WebConsoleUtil.getVariableResolver(request));
        vars.put("smart.logger.list.title", "Select a log file");
        response.getWriter().print(template);
    }
}
