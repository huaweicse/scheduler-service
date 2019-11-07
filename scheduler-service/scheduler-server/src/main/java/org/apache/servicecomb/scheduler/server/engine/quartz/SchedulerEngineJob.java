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
