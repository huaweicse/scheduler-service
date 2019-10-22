package org.apache.servicecomb.scheduler.common;

import java.util.HashMap;
import java.util.Map;

public class JobMeta {
    public static final String PROPERTY_CRON = "x_cron";
    public static final String PROPERTY_DESC = "x_description";
    public static final String PROPERTY_EXISTS = "x_exists";

    private String groupName;
    private String jobName;
    private Map<String, String> properties = new HashMap<>();

    public void addProperty(String k, String v) {
        properties.put(k, v);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getProperty(String k) {
        return properties.get(k);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public JobMeta() {

    }

    public JobMeta(String jobName, String groupName) {
        this.jobName = jobName;
        this.groupName = groupName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobMeta jobMeta = (JobMeta) o;

        if (groupName != null ? !groupName.equals(jobMeta.groupName) : jobMeta.groupName != null) return false;
        return jobName != null ? jobName.equals(jobMeta.jobName) : jobMeta.jobName == null;
    }

    @Override
    public int hashCode() {
        int result = groupName != null ? groupName.hashCode() : 0;
        result = 31 * result + (jobName != null ? jobName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JobMeta:{groupName=" + groupName + ",jobNmae=" + jobName + "}";
    }
}
