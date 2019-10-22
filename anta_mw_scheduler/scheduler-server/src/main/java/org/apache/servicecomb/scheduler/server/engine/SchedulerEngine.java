package org.apache.servicecomb.scheduler.server.engine;

import org.apache.servicecomb.scheduler.common.Job;
import org.apache.servicecomb.scheduler.common.JobMeta;

public interface SchedulerEngine {
    boolean scheduleJob(JobMeta jobMeta, ExecutionEngine engine);

    boolean stopJob(JobMeta jobMeta);

    boolean checkExists(JobMeta meta);
}
