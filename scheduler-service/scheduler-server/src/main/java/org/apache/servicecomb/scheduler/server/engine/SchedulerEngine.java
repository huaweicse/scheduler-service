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

import java.util.List;

import org.apache.servicecomb.scheduler.common.JobMeta;
import org.apache.servicecomb.scheduler.common.ServiceDataResponse;
import org.apache.servicecomb.scheduler.common.ServiceResponse;

public interface SchedulerEngine {
  ServiceResponse createJob(JobMeta jobMeta);

  ServiceResponse scheduleJob(String jobName, String jobGroup, ExecutionEngine engine);

  ServiceResponse unscheduleJob(String jobName, String jobGroup);

  boolean checkScheduled(String jobName, String jobGroup);

  boolean checkExists(String jobName, String jobGroup);

  ServiceDataResponse<JobMeta, Void> getJobMeta(String jobName, String jobGroup);

  ServiceDataResponse<List<JobMeta>, Void> getAllJobs();
}
