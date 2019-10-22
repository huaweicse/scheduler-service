package org.apache.servicecomb.scheduler.server.engine.quartz;

import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.apache.servicecomb.scheduler.common.JobMeta;
import org.apache.servicecomb.scheduler.server.engine.ExecutionEngine;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerEngineJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerEngineJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail detail = context.getJobDetail();
        ExecutionEngine engine = (ExecutionEngine) BeanUtils.getBean(ExecutionEngine.BEAN_NAME);
        JobMeta jobMeta = new JobMeta();
        jobMeta.setJobName(detail.getKey().getName());
        jobMeta.setGroupName(detail.getKey().getGroup());
        engine.trigger(jobMeta);

        LOGGER.info("executing : " + jobMeta.toString());
    }

}
