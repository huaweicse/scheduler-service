package org.apache.servicecomb.scheduler.common;

public enum JobStatus {
    INIT,
    FAILED_START_CLIENT_BUSY, // client busy
    FAILED_START_CLIENT_RESTARTED, // client restarted and job status lost
    FAILED_START_NO_CLIENT,
    FAILED_START_INVOKE,
    EXECUTING,
    FAILED_EXECUTING_CLIENT_ERROR, // client execution task error
    FAILED_WITH_ERROR,
    FINISHED
}
