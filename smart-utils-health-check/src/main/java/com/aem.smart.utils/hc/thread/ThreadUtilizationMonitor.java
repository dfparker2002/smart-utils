package com.aem.smart.utils.hc.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread monitor utility
 */
class ThreadUtilizationMonitor {

    static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtilizationMonitor.class);

    final ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();

    /**
     * Watch threads and return utilization information, when total thread utilization threshold will be exceeded
     */
    public TotalThreadsUtilization monitor(long watchInterval, double threshold) throws InterruptedException {
        ThreadsSnapshot oldStats = getThreadsStats();
        TotalThreadsUtilization totalTime = new TotalThreadsUtilization();
        while (!Thread.currentThread().isInterrupted()) {
            Thread.sleep(watchInterval);
            ThreadsSnapshot newStats = getThreadsStats();
            totalTime = getThreadTotalTime(oldStats, newStats);
            LOGGER.debug("monitor(): total cpu usage={}, threshold={}", totalTime.threadTotalTime, threshold);
            if (totalTime.threadTotalTime >= threshold) {
                break;
            }
        }
        return totalTime;
    }

    private ThreadsSnapshot getThreadsStats() {
        Collection<Thread> threads = Thread.getAllStackTraces().keySet();
        ThreadsSnapshot threadsSnapshot = new ThreadsSnapshot();
        for (Thread thread : threads) {
            long threadId = thread.getId();
            long time = mxBean.getThreadCpuTime(threadId);
            threadsSnapshot.addThread(thread, time);
        }
        return threadsSnapshot;
    }

    private TotalThreadsUtilization getThreadTotalTime(ThreadsSnapshot oldStats, ThreadsSnapshot newStats) {
        TotalThreadsUtilization totalTime = new TotalThreadsUtilization();
        double timeInterval = newStats.time - oldStats.time;
        for (Thread th : newStats.threadTime.keySet()) {
            long oldTime = oldStats.getTime(th);
            long newTime = newStats.getTime(th);
            double usage = (newTime - oldTime) / timeInterval;
            ThreadUtilization threadUtilization = new ThreadUtilization(th, usage);
            totalTime.add(threadUtilization);
        }
        return totalTime;
    }

    static class ThreadsSnapshot {

        final long time;
        Map<Thread, Long> threadTime = new HashMap<>();

        public ThreadsSnapshot() {
            this.time = System.nanoTime();
        }

        void addThread(Thread thread, long time) {
            this.threadTime.put(thread, time);
        }

        long getTime(Thread thread) {
            Long time = threadTime.get(thread);
            if (time == null) {
                return 0;
            }
            return time;
        }
    }
}
