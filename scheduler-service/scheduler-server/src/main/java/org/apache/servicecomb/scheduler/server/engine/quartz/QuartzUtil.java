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

import java.io.IOException;

import org.apache.servicecomb.scheduler.common.JobMeta;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class QuartzUtil {
  private static final String JOB_META_KEY = "x_job_meta_key";

  private static final ObjectMapper JSON = new ObjectMapper();

  private QuartzUtil() {
  }

  public static JobDetail jobMeta2JobDetail(JobMeta jobMeta) {
    try {
      return JobBuilder.newJob().withIdentity(jobMeta.getJobName(), jobMeta.getGroupName())
          .usingJobData(JOB_META_KEY, JSON.writeValueAsString(jobMeta))
          .storeDurably(true)
          .ofType(SchedulerEngineJob.class)
          .build();
    } catch (JsonProcessingException e) {
      // this will not happen
      throw new RuntimeException(e);
    }
  }

  public static JobMeta jobDetail2JobMeta(JobDetail jobDetail) {
    try {
      return JSON.readValue(jobDetail.getJobDataMap().getString(JOB_META_KEY), JobMeta.class);
    } catch (IOException e) {
      // this will not happen
      throw new RuntimeException(e);
    }
  }
}
