package com.aem.smart.utils.hc.thread;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This test uses heuristics in order to check thread utilization, so it's possible that it may occasionally breaks
 */
public class ThreadUtilizationMonitorTest {

    static String THREAD_NAME = "ThreadUtilizationMonitorTest";

    static Thread thread;

    @BeforeClass
    public static void setUp() throws Exception {
        thread = startCpuConsumptionThread();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        thread.interrupt();
    }

    @Test
    public void testMonitor() throws Exception {
        ThreadUtilizationMonitor monitor = new ThreadUtilizationMonitor();
        TotalThreadsUtilization threadsUtilization = monitor.monitor(50, 0.0);
        for(ThreadUtilization tu : threadsUtilization.threadsUtilization) {
            if(!THREAD_NAME.equals(tu.thread.getName())) {
                continue;
            }
            assertTrue("thread utilization must be greater than zero (probably ~ 0.9", tu.utilization > 0.0);
            assertTrue("total threads utilization must be greater than single thread", threadsUtilization.threadTotalTime >= tu.utilization);
            String nonEmptyReport = threadsUtilization.toString(0.0);
            assertFalse(nonEmptyReport.isEmpty());
            String emptyReport = threadsUtilization.toString(999);
            assertTrue(emptyReport.isEmpty());
            return;
        }
        fail();
    }

    private static Thread startCpuConsumptionThread() {
        Thread thread = new Thread(new Runnable() {
            volatile long value = 0;
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    ++value;
                }
            }
            /**
             * use value, in order to prevent compiler optimization
             */
            @Override
            public String toString() {
                return "$classname{" +
                        "value=" + value +
                        '}';
            }
        });
        thread.setName(THREAD_NAME);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
}
