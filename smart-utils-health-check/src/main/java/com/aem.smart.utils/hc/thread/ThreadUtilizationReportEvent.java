package com.aem.smart.utils.hc.thread;

import com.google.common.collect.ImmutableMap;
import org.osgi.service.event.Event;

import java.util.Objects;

/**
 * Handy event for posting thread utilization report
 */
public class ThreadUtilizationReportEvent extends Event {

    static final String TOPIC = "com/fglsports/hc/thread/utilizationReport/CREATED";
    static final String REPORT_KEY = "report";

    public ThreadUtilizationReportEvent(CharSequence report) {
        super(TOPIC, ImmutableMap.of(REPORT_KEY, Objects.requireNonNull(report)));
    }
}
