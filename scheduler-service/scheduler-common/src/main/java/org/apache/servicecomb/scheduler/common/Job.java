package org.apache.servicecomb.scheduler.common;

public interface Job {
    String METHOD_START = "start";
    String METHOD_POLL = "poll";

    JobContext start(JobContext context);

    JobContext poll(JobContext context);
}
