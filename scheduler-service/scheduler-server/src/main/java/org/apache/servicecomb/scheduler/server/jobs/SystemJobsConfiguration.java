package org.apache.servicecomb.scheduler.server.jobs;

import org.apache.servicecomb.scheduler.common.JobMeta;
import org.apache.servicecomb.scheduler.server.engine.ExecutionEngine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemJobsConfiguration {
    @Bean(ExecutionEngine.BEAN_POLLER_JOB)
    public JobMeta pollerJob() {
        JobMeta pollerTask = new JobMeta();
        pollerTask.setGroupName("_pollerGroup");
        pollerTask.setJobName("_pollerJob");
        pollerTask.addProperty(JobMeta.PROPERTY_CRON, "*/5 * * * * ?");
        pollerTask.addProperty(JobMeta.PROPERTY_DESC, "scheduler内建查询任务状态接口");
        return pollerTask;
    }

}
