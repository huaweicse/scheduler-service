package org.apache.servicecomb.scheduler.server.jobslogger;

import org.apache.servicecomb.scheduler.common.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobLogger.class);

    public static void logJob(JobContext context) {
        LOGGER.info("groupName={},jobName={},executionId={},status={}", context.getJobMeta().getGroupName(),
                context.getJobMeta().getJobName(), context.getExecutionID(), context.getJobStatus());
    }
}
