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

package org.apache.servicecomb.scheduler.common;

import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ClientJobExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientJobExecutor.class);
    private Map<String, JobContext> tasks = new ConcurrentHashMap<>();

    private ExecutorService executorService = new ThreadPoolExecutor(2, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(10),
            new ThreadFactory() {
                private AtomicInteger counter = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "client-job-executor-" + counter.getAndIncrement()) {
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

    public JobContext executeJob(JobContext context, AbstractClientJob job) {
        if (context.getExecutionID() == null || context.getJobMeta() == null || context.getJobStatus() != JobStatus.INIT) {
            throw new InvocationException(400, "bad param", "bad param");
        }

        try {
            executorService.submit(new Task(job, context));
            context.setJobStatus(JobStatus.EXECUTING);
            tasks.put(context.getExecutionID(), context);
        } catch (RejectedExecutionException e) {
            context.setJobStatus(JobStatus.FAILED_START_CLIENT_BUSY);
        }

        return context;
    }

    public JobContext pollJob(JobContext context, AbstractClientJob job) {
        if (context.getExecutionID() == null) {
            throw new InvocationException(400, "bad param", "execution id is null");
        }
        if (context.getJobStatus() != JobStatus.EXECUTING) {
            throw new InvocationException(400, "bad param", "inconsistence state");
        }

        JobContext savedContext = tasks.get(context.getExecutionID());
        if (savedContext == null) {
            context.setJobStatus(JobStatus.FAILED_START_CLIENT_RESTARTED);
        } else {
            context.setJobStatus(savedContext.getJobStatus());
            if (context.getJobStatus() != JobStatus.EXECUTING) {
                tasks.remove(context.getExecutionID());
            }
        }

        return context;
    }

    class Task implements Runnable {
        AbstractClientJob job;
        JobContext context;

        Task(AbstractClientJob job, JobContext context) {
            this.job = job;
            this.context = context;
        }

        @Override
        public void run() {
            try {
                job.run(context);
                context.setJobStatus(JobStatus.FINISHED);
            } catch (Exception e) {
                LOGGER.error("run job [{}] error", context.toString(), e);
                context.setJobStatus(JobStatus.FAILED_EXECUTING_CLIENT_ERROR);
            }
        }
    }
}
