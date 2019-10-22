package org.apache.servicecomb.scheduler.server.engine;

import org.apache.servicecomb.scheduler.common.JobContext;

import java.util.Random;

public class SchedulerContext {
    private static final long POLL_TIMEOUT = 30 * 60 * 1000; //30 minutes
    private static final int MAX_FAILED_COUNT = 10;

    private long startTime;
    private int serverIndex;
    private int pollCount;
    private int pollFailedCount;
    private String currentEndpoint;
    private JobContext jobContext;

    public SchedulerContext(JobContext context) {
        jobContext = context;
        startTime = System.currentTimeMillis();
        serverIndex = new Random().nextInt(10000);
        pollCount = 0;
        pollFailedCount = 0;
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() - startTime > POLL_TIMEOUT;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getAndIncrementServerIndex() {
        return serverIndex++;
    }

    public boolean maxPollErrorReached() {
        return this.pollFailedCount > MAX_FAILED_COUNT;
    }

    public int getPollCount() {
        return pollCount;
    }

    public int getPollFailedCount() {
        return pollFailedCount;
    }

    void incrementFailedPollCount() {
        pollCount++;
        pollFailedCount++;
    }

    void incrementSuccessPollCount() {
        pollCount++;
    }

    public JobContext getJobContext() {
        return jobContext;
    }

    public String getCurrentEndpoint() {
        return currentEndpoint;
    }

    public void setCurrentEndpoint(String currentEndpoint) {
        this.currentEndpoint = currentEndpoint;
    }
}
