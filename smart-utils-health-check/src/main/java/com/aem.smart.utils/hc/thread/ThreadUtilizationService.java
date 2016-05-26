package com.aem.smart.utils.hc.thread;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * Thread monitoring service.
 * It watch active threads, and log their stacktrace with cpu utilization, in case if specified threshold was exceed
 */
@Component(
        immediate = true,
        metatype = true,
        label = "Sport Chek - Thread monitor",
        description = "Thread monitor for logging threads that violate specified threshold"
)
public class ThreadUtilizationService implements Runnable {

    static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtilizationService.class);

    @Property(label = "Monitoring time", description = "Time interval for monitoring thread activity, (milliseconds)", longValue = 10000)
    static final String STAT_WATCH_INTERVAL = "stat.watch.interval";

    @Property(label = "Monitoring delay", description = "Delay, before start next monitoring after threshold violation, (milliseconds)", longValue = 10000)
    static final String STAT_NEXT_WATCH_DELAY = "stat.next.watch.delay";

    @Property(label = "Total threshold", description = "Total threshold for all thread cpu usage", doubleValue = 0.7)
    static final String TOTAL_UTILIZATION_THRESHOLD = "total.utilization.threshold";

    @Property(label = "Thread threshold", description = "Threshold for single thread cpu usage (such threads will be logged", doubleValue = 0.01)
    static final String THREAD_UTILIZATION_THRESHOLD = "thread.utilization.threshold";

    @Reference
    private EventAdmin eventAdmin;

    final ThreadUtilizationMonitor threadUtilizationMonitor = new ThreadUtilizationMonitor();
    private Thread thread;

    private long statWatchInterval = 10000;
    private long nextWatchDelay = 10000;
    private double totalUtilizationThreshold = 0.0007;
    private double threadUtilizationThreshold = 0.0001;

    @Activate
    void activate(Map<String, Object> properties) {
        Objects.requireNonNull(this.eventAdmin, "EventAdmin reference must not be null");

        this.statWatchInterval = (long) properties.get(STAT_WATCH_INTERVAL);
        this.nextWatchDelay = (long) properties.get(STAT_NEXT_WATCH_DELAY);
        this.totalUtilizationThreshold = (double) properties.get(TOTAL_UTILIZATION_THRESHOLD);
        this.threadUtilizationThreshold = (double) properties.get(THREAD_UTILIZATION_THRESHOLD);

        this.thread = new Thread(this);
        this.thread.setDaemon(true);
        this.thread.setName(getClass().getName());
        this.thread.start();
        LOGGER.debug("activated() {}", this);
    }

    @Deactivate
    void deactivate() throws InterruptedException {
        LOGGER.debug("deactivate()");
        this.thread.interrupt();
    }

    @Override
    public void run() {
        LOGGER.debug("Start monitoring");
        try {
            monitorAndLogCpuUsage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LOGGER.debug("Stop monitoring");
    }

    void monitorAndLogCpuUsage() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            TotalThreadsUtilization utilizationReport =
                    threadUtilizationMonitor.monitor(statWatchInterval, totalUtilizationThreshold);
            prepareAndLogUtilizationReport(utilizationReport);
            Thread.sleep(nextWatchDelay);
        }
    }

    private void prepareAndLogUtilizationReport(TotalThreadsUtilization utilization) {
        String threadUtilizationReport = utilization.toString(threadUtilizationThreshold);
        if (threadUtilizationReport.isEmpty()) {
            LOGGER.warn("It seems that " + THREAD_UTILIZATION_THRESHOLD
                    + " value (" + threadUtilizationThreshold + ") "
                    + "is too high, so report is empty");
        } else {
            StringBuilder report = new StringBuilder();
            report.append("Threads utilization report. Threshold=");
            report.append(String.format("%2.5f", totalUtilizationThreshold));
            report.append('\n');
            report.append(threadUtilizationReport);
            report.append("End of report");
            LOGGER.info("\n{}", report);
            sendUtilizationReport(report);
        }
    }

    private void sendUtilizationReport(CharSequence report) {
        eventAdmin.postEvent(new ThreadUtilizationReportEvent(report));
    }

    @Override
    public String toString() {
        return "ThreadUtilizationService{" +
                "statWatchInterval=" + statWatchInterval +
                ", nextWatchDelay=" + nextWatchDelay +
                ", totalUtilizationThreshold=" + totalUtilizationThreshold +
                ", threadUtilizationThreshold=" + threadUtilizationThreshold +
                '}';
    }
}
