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
package org.apache.servicecomb.scheduler.server.jobs;

import org.apache.servicecomb.scheduler.common.JobMeta;
import org.apache.servicecomb.scheduler.server.engine.ExecutionEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemJobsConfiguration {
  public static final String SYSTM_JOB_GROUP = "x_system";

  public static final String POLLER_JOB = "x_poller";

  @Bean(ExecutionEngine.BEAN_POLLER_JOB)
  public JobMeta pollerJob() {
    JobMeta pollerJob = new JobMeta();
    pollerJob.setGroupName(SYSTM_JOB_GROUP);
    pollerJob.setJobName(POLLER_JOB);
    pollerJob.addProperty(JobMeta.PROPERTY_CRON, "*/5 * * * * ?");
    pollerJob.addProperty(JobMeta.PROPERTY_DESC, "polling job status");
    return pollerJob;
  }
}
