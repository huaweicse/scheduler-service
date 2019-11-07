/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicecomb.scheduler.server.engine;

import org.apache.servicecomb.loadbalance.filter.ServerDiscoveryFilter;
import org.apache.servicecomb.scheduler.common.*;
import org.apache.servicecomb.scheduler.server.jobslogger.JobLogger;
import org.apache.servicecomb.serviceregistry.discovery.DiscoveryFilter;
import org.apache.servicecomb.serviceregistry.discovery.DiscoveryTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component("ExecutionEngine")
public class ExecutionEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionEngine.class);

    public static final String BEAN_NAME = "ExecutionEngine";
    public static final String BEAN_POLLER_JOB = "PollerJob";

    private Map<JobMeta, JobContext> jobs = new ConcurrentHashMap<>();
    private Map<JobMeta, SchedulerContext> pollingJobs = new ConcurrentHashMap<>();

    private DiscoveryTree discoveryTree = new DiscoveryTree();

    private ExecutorService executorService = Executors.newScheduledThreadPool(20, new ThreadFactory() {
        private AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "execution-engine-task-" + counter.getAndIncrement()) {
                @Override
                public void run() {
                    try {
                        super.run();
                    } catch (Throwable e) {
                        LOGGER.error("", e);
                    }
                }
            };
        }
    });

    private ExecutorService pollerExecutorService = Executors.newFixedThreadPool(10, new ThreadFactory() {
        private AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "execution-engine-poller-task-" + counter.getAndIncrement()) {
                @Override
                public void run() {
                    try {
                        super.run();
                    } catch (Throwable e) {
                        LOGGER.error("", e);
                    }
                }
            };
        }
    });

    private IAlarm alarm;

    private JobMeta pollerTask;

    public ExecutionEngine(@Autowired IAlarm alarm, @Autowired @Qualifier(BEAN_POLLER_JOB) JobMeta pollerTask) {
        this.alarm = alarm;
        this.pollerTask = pollerTask;

        this.discoveryTree.loadFromSPI(DiscoveryFilter.class);
        this.discoveryTree.addFilter(new ServerDiscoveryFilter());
        this.discoveryTree.sort();
    }

    public JobMeta getPollerTask() {
        return pollerTask;
    }

    public List<JobContext> getRunningJobs() {
       return Arrays.asList(jobs.values().toArray(new JobContext[0]));
    }

    public void trigger(JobMeta jobMeta) {
        if (pollerTask.equals(jobMeta)) {
            handlePollerTask();
            return;
        }

        JobContext oldContext = jobs.get(jobMeta);
        if (oldContext != null && oldContext.getJobStatus() != JobStatus.FINISHED) {
            AlarmContext alarmContext = new AlarmContext();
            alarmContext.setAlarmLevel(AlarmLevel.WARN);
            alarmContext.setJobContext(oldContext);
            alarm.sendAlarm(alarmContext);
            return;
        }
        JobContext context = new JobContext();
        context.setExecutionID(UUID.randomUUID().toString());
        context.setJobMeta(jobMeta);
        context.setJobStatus(JobStatus.INIT);
        jobs.put(jobMeta, context);

        CompletableFuture<SchedulerContext> callback = new CompletableFuture<SchedulerContext>();
        ExecutionTask task = new ExecutionTask(new SchedulerContext(context), callback, discoveryTree);
        executorService.submit(task);
        JobLogger.logJob(context);

        callback.whenComplete((r, e) -> {
            if (callback.isCompletedExceptionally()) {
                // would not happen, just logging
                jobs.remove(jobMeta);
                LOGGER.error("executing job [{}] error", context.toString(), e);
            } else {
                if (r.getJobContext().getJobStatus() == JobStatus.EXECUTING) {
                    pollingJobs.put(jobMeta, r);
                } else {
                    JobLogger.logJob(jobs.remove(jobMeta));
                    LOGGER.warn("executing job[{}] unknown status", context.toString());
                }
            }
        });
    }

    public void handlePollerTask() {
        for (JobMeta jobMeta : pollingJobs.keySet()) {
            CompletableFuture<SchedulerContext> callback = new CompletableFuture<SchedulerContext>();
            PollerTask pollerTask = new PollerTask(pollingJobs.get(jobMeta), callback, discoveryTree);
            pollerExecutorService.submit(pollerTask);

            callback.whenComplete((r, e) -> {
                if (callback.isCompletedExceptionally()) {
                    pollingJobs.remove(jobMeta);
                    JobContext context = jobs.remove(jobMeta);
                    JobLogger.logJob(context);
                    LOGGER.error("polling job [{}] error", context.toString(), e);
                } else {
                    if (r.getJobContext().getJobStatus() == JobStatus.FINISHED) {
                        pollingJobs.remove(jobMeta);
                        JobContext jobContext = jobs.remove(jobMeta);
                        jobContext.setJobStatus(JobStatus.FINISHED);
                        JobLogger.logJob(jobContext);
                        return;
                    }

                    if(r.getJobContext().getJobStatus() != JobStatus.EXECUTING) {
                        pollingJobs.remove(jobMeta);
                        JobContext jobContext = jobs.remove(jobMeta);
                        jobContext.setJobStatus(r.getJobContext().getJobStatus());
                        JobLogger.logJob(jobContext);
                        return;
                    }

                    if (r.isTimeout() || r.maxPollErrorReached()) {
                        pollingJobs.remove(jobMeta);
                        JobContext jobContext = jobs.remove(jobMeta);
                        jobContext.setJobStatus(JobStatus.FAILED_WITH_ERROR);
                        JobLogger.logJob(jobContext);
                        AlarmContext alarmContext = new AlarmContext();
                        alarmContext.setAlarmLevel(AlarmLevel.ERROR);
                        alarmContext.setJobContext(r.getJobContext());
                        if (r.isTimeout()) {
                            alarmContext.setAlarmMessage("poll job timeout.");
                        } else {
                            alarmContext.setAlarmMessage("poll job meet max error.");
                        }
                        alarm.sendAlarm(alarmContext);
                    }
                }
            });
        }
    }
}
