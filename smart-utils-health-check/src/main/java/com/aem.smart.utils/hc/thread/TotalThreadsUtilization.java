package com.aem.smart.utils.hc.thread;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represent CPU utilization by all threads
 */
class TotalThreadsUtilization {

    double threadTotalTime;
    List<ThreadUtilization> threadsUtilization = new ArrayList<>();

    void add(ThreadUtilization threadUtilization) {
        this.threadsUtilization.add(threadUtilization);
        this.threadTotalTime += threadUtilization.utilization;
    }

    /**
     * Get stack traces for all threads with utilization larger that threshold
     */
    @Override
    public String toString() {
        return "TotalThreadsUtilization{" +
                "threadTotalTime=" + threadTotalTime +
                ", threadsUtilization=" + threadsUtilization +
                '}';
    }

    /**
     * Get stack traces for all threads with utilization larger that threshold
     */
    String toString(double utilizationThreshold) {
        List<ThreadUtilization> threadsUtilization = new ArrayList<>(this.threadsUtilization);
        Collections.sort(threadsUtilization);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(outputStream);
        for (ThreadUtilization threadUtilization : threadsUtilization) {
            if (shouldSkipThread(utilizationThreshold, threadUtilization)) {
                continue;
            }
            StackTraceElement[] stackTrace = threadUtilization.thread.getStackTrace();
            logThreadTitle(stream, threadUtilization);
            logThreadStackTrace(stream, stackTrace);
        }
        return outputStream.toString();
    }

    private void logThreadStackTrace(PrintStream stream, StackTraceElement[] stackTrace) {
        for (StackTraceElement element : stackTrace) {
            stream.print("     ");
            stream.println(element);
        }
    }

    private void logThreadTitle(PrintStream stream, ThreadUtilization threadUtilization) {
        stream.printf("Thread: [%s], State: [%s], CPU=%2.5f\n",
                threadUtilization.thread.getName(),
                threadUtilization.thread.getState(),
                threadUtilization.utilization
        );
    }

    private boolean shouldSkipThread(double utilizationThreshold, ThreadUtilization threadUtilization) {
        return threadUtilization.utilization < utilizationThreshold;
    }
}
