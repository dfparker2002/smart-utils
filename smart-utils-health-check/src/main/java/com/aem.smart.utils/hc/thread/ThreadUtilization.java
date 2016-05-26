package com.aem.smart.utils.hc.thread;

/**
 * Represent CPU utilization by single thread
 */
class ThreadUtilization implements Comparable<ThreadUtilization> {

    Thread thread;
    double utilization;

    public ThreadUtilization(Thread thread, double utilization) {
        this.thread = thread;
        this.utilization = utilization;
    }

    @Override
    public int compareTo(ThreadUtilization that) {
        return Double.compare(that.utilization, this.utilization);
    }

    @Override
    public String toString() {
        return "ThreadUtilization{" +
                "thread=" + thread.getName() +
                ", utilization=" + String.format("%10f", utilization) +
                '}';
    }
}
