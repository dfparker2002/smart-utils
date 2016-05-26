package com.aem.smart.utils.hc.thread;

import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.api.HealthCheck;
import org.apache.sling.hc.api.Result;
import org.apache.sling.hc.util.FormattingResultLog;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import java.util.Date;

/**
 * This health check just watches thread utilization report generation, and store locally the last one.
 * Probably, we should clean it after some period of time, in order to not confuse users with old cpu utilization
 * data
 */
@SlingHealthCheck(
        name = "Thread utilization healthcheck",
        label = "Thread utilization Health Check",
        description = "Show last available report about thread utilization, in case if specified threshold was exceed.",
        tags = {"availability", "threads"},
        generateService = false
)
@Property(
        name = org.osgi.service.event.EventConstants.EVENT_TOPIC,
        value = ThreadUtilizationReportEvent.TOPIC
)
@Service({HealthCheck.class, EventHandler.class})
public class ThreadUtilizationHealthCheck implements HealthCheck, EventHandler {

    private volatile CharSequence lastThreadUtilizationReport = "";

    @Override
    public Result execute() {
        final Result result;
        CharSequence report = lastThreadUtilizationReport;
        if (report == null || report.length() == 0) {
            result = new Result(Result.Status.OK, "No report about threshold excess.");
        } else {
            final FormattingResultLog log = new FormattingResultLog();
            String[] reportLines = report.toString().split("\n");
            for(String line : reportLines) {
                log.warn("{}", line);
            }
            result = new Result(log);
        }
        return result;
    }

    @Override
    public void handleEvent(Event event) {
        this.lastThreadUtilizationReport = "[" + new Date().toString() + "] " +
                event.getProperty(ThreadUtilizationReportEvent.REPORT_KEY);
    }
}
