package org.apache.servicecomb.scheduler.common;

public class JobContext {
    private String executionID;
    private JobMeta jobMeta;
    private JobStatus jobStatus;

    public String getExecutionID() {
        return executionID;
    }

    public void setExecutionID(String executionID) {
        this.executionID = executionID;
    }

    public JobMeta getJobMeta() {
        return jobMeta;
    }

    public void setJobMeta(JobMeta jobMeta) {
        this.jobMeta = jobMeta;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    @Override
    public String toString() {
        return "JobContext:{executionID=" + executionID + ",jobMeta=" + jobMeta.toString()
                + ",jobStatus=" + jobStatus + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobContext that = (JobContext) o;

        if (!executionID.equals(that.executionID)) return false;
        return jobMeta.equals(that.jobMeta);
    }

    @Override
    public int hashCode() {
        int result = executionID.hashCode();
        result = 31 * result + jobMeta.hashCode();
        return result;
    }
}
