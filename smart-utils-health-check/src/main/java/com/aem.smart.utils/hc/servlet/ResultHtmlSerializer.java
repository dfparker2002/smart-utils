/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.aem.smart.utils.hc.servlet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.api.Result;
import org.apache.sling.hc.api.ResultLog.Entry;
import org.apache.sling.hc.api.execution.HealthCheckExecutionResult;
import org.osgi.service.component.ComponentContext;

import com.google.common.collect.Iterators;

/** Serializes health check results into html format. */
@Service(ResultHtmlSerializer.class)
@Component(metatype = true)
public class ResultHtmlSerializer {

    private static final String CSS_STYLE_DEFAULT = "body { font-size:12px; font-family:arial,verdana,sans-serif;background-color:#FFFDF1; }\n"
            + "h1 { font-size:20px;}\n" + "table { font-size:12px; border:#ccc 1px solid; border-radius:3px; }\n"
            + "table th { padding:5px; text-align: left; background: #ededed; }\n"
            + "table td { padding:5px; border-top: 1px solid #ffffff; border-bottom:1px solid #e0e0e0; border-left: 1px solid #e0e0e0; }\n"
            + ".statusOK { background-color:#CCFFCC;}\n" + ".statusWARN { background-color:#FFE569;}\n"
            + ".statusCRITICAL { background-color:#F0975A;}\n"
            + ".statusHEALTH_CHECK_ERROR { background-color:#F16D4E;}\n" + ".helpText { color:grey; font-size:80%; }\n";

    private static final String PROPERTY_CSS_STYLE = "styleString";

    @Property(name = PROPERTY_CSS_STYLE, label = "CSS Style", description = "CSS Style - can be configured to change the look and feel of the html result page.", value = CSS_STYLE_DEFAULT)
    private String styleString;

    @Activate
    protected final void activate(final ComponentContext context) {
        final Dictionary<?, ?> properties = context.getProperties();
        this.styleString = PropertiesUtil.toString(properties.get(PROPERTY_CSS_STYLE), CSS_STYLE_DEFAULT);
    }

    public String serialize(final Result overallResult, final List<HealthCheckExecutionResult> executionResults,
            String escapedHelpText, boolean includeDebug) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        writer.println("<html><head><title>System Health</title>"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' /><style>" + styleString
                + "</style></head><body><h1>System Health</h1>");

        writer.println("<p><span class=\"" + getClassForStatus(overallResult.getStatus())
                + "\"><strong>Overall Result: " + overallResult.getStatus() + "</strong></span></p>");

        final DateFormat dfShort = new SimpleDateFormat("HH:mm:ss.SSS");
        final DateFormat dfLong = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        writer.println("<table id=\"healthCheckResults\" cellspacing=\"0\">");
        writer.println(
                "<thead><tr><th>Health Check</th><th>Status</th><th>Log</th><th colspan=\"2\">Execution Time</th></tr></thead>");
        for (HealthCheckExecutionResult executionResult : executionResults) {
            Result result = executionResult.getHealthCheckResult();
            writer.println("<tr class=\"" + getClassForStatus(result.getStatus()) + "\" " + "title=\"Tags: "
                    + StringEscapeUtils.escapeHtml(
                            StringUtils.join(executionResult.getHealthCheckMetadata().getTags(), ","))
                    + "\">");
            writer.println("<td><span title=\""
                    + StringEscapeUtils.escapeHtml(executionResult.getHealthCheckMetadata().getName()) + "\">"
                    + StringEscapeUtils.escapeHtml(executionResult.getHealthCheckMetadata().getTitle())
                    + "</span></td>");
            writer.println("<td style='font-weight:bold;'>"
                    + StringEscapeUtils.escapeHtml(result.getStatus().toString()) + "</td>");
            writer.println("<td>");
            boolean isFirst = true;

            boolean isSingleResult = isSingleResult(result);

            for (Entry entry : result) {
                if (!includeDebug && entry.getStatus() == Result.Status.DEBUG) {
                    continue;
                }

                if (isFirst) {
                    isFirst = false;
                } else {
                    writer.println("<br/>\n");
                }

                boolean showStatus = !isSingleResult && entry.getStatus() != Result.Status.DEBUG
                        && entry.getStatus() != Result.Status.INFO;

                String message = StringEscapeUtils.escapeHtml(entry.getMessage());
                if (entry.getStatus() == Result.Status.DEBUG) {
                    message = "<span style='color:gray'/>" + message + "</span>";
                }
                writer.println(
                        (showStatus ? StringEscapeUtils.escapeHtml(entry.getStatus().toString()) + " " : "") + message);

                Exception exception = entry.getException();
                if (exception != null) {
                    writer.println("<span style='width:20px'/>" + StringEscapeUtils.escapeHtml(exception.toString()));
                    writer.println("<!--");
                    exception.printStackTrace(writer);
                    writer.println("-->");
                }
            }
            writer.println("</td>");
            Date finishedAt = executionResult.getFinishedAt();
            writer.println(
                    "<td>" + (isToday(finishedAt) ? dfShort.format(finishedAt) : dfLong.format(finishedAt)) + "</td>");
            writer.println("<td>" + executionResult.getElapsedTimeInMs() + "</td>");

            writer.println("</tr>");
        }
        writer.println("</table>");

        writer.println("<div class='helpText'>");
        writer.println(escapedHelpText);
        writer.println("</div>");
        writer.println("</body></html>");

        return stringWriter.toString();

    }

    private String getClassForStatus(final Result.Status status) {
        return "status" + status.name();
    }

    private boolean isSingleResult(final Result result) {
        return Iterators.size(result.iterator()) == 1;
    }

    private boolean isToday(Date date) {
        LocalDate today = LocalDate.now();
        LocalDate argDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return today.compareTo(argDate) == 0;

    }
}
