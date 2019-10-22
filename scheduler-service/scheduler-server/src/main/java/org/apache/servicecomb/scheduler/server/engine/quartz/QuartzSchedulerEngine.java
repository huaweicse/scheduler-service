package org.apache.servicecomb.scheduler.server.engine.quartz;

import org.apache.servicecomb.scheduler.common.JobMeta;
import org.apache.servicecomb.scheduler.server.engine.ExecutionEngine;
import org.apache.servicecomb.scheduler.server.engine.SchedulerEngine;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QuartzSchedulerEngine implements SchedulerEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzSchedulerEngine.class);

    private Scheduler scheduler;

    public QuartzSchedulerEngine() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            // and start it off
            scheduler.start();
        } catch (SchedulerException e) {
            LOGGER.error("", e);
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    public boolean scheduleJob(JobMeta jobMeta, ExecutionEngine engine) {
        try {
            JobDetail jobDetail = JobBuilder.newJob().withIdentity(jobMeta.getJobName(), jobMeta.getGroupName())
                    .ofType(SchedulerEngineJob.class)
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobMeta.getProperty(JobMeta.PROPERTY_CRON))).build();
            scheduler.scheduleJob(jobDetail, trigger);
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean stopJob(JobMeta jobMeta) {
        try {
            return scheduler.deleteJob(new JobKey(jobMeta.getJobName(), jobMeta.getGroupName()));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkExists(JobMeta jobMeta) {
        try {
            return scheduler.checkExists(new JobKey(jobMeta.getJobName(), jobMeta.getGroupName()));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }
}
