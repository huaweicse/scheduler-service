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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.servicecomb.scheduler.common.ErrorCode;
import org.apache.servicecomb.scheduler.common.JobMeta;
import org.apache.servicecomb.scheduler.common.ServiceDataResponse;
import org.apache.servicecomb.scheduler.common.ServiceResponse;
import org.apache.servicecomb.scheduler.server.engine.ExecutionEngine;
import org.apache.servicecomb.scheduler.server.engine.SchedulerEngine;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
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
  public ServiceResponse createJob(JobMeta jobMeta) {
    ServiceResponse response = ServiceResponse.newSuccessServiceResponse();
    try {
      JobDetail jobDetail = QuartzUtil.jobMeta2JobDetail(jobMeta);
      scheduler.addJob(jobDetail, true);
    } catch (SchedulerException e) {
      LOGGER.error("", e);
      response.setSuccess(false);
      response.setErrorCode(ErrorCode.ERROR_PARAMETER_NOT_VALID);
      response.setErrorMessage(e.getMessage());
    }
    return response;
  }

  @Override
  public ServiceResponse scheduleJob(String jobName, String jobGroup, ExecutionEngine engine) {
    ServiceResponse response = ServiceResponse.newSuccessServiceResponse();
    try {
      if (checkScheduled(jobName, jobGroup)) {
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroup));
      }
      JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroup));
      JobMeta jobMeta = QuartzUtil.jobDetail2JobMeta(jobDetail);
      Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup)
          .withSchedule(CronScheduleBuilder.cronSchedule(jobMeta.getProperty(JobMeta.PROPERTY_CRON))).build();
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException e) {
      LOGGER.error("", e);
      response.setSuccess(false);
      response.setErrorCode(ErrorCode.ERROR_PARAMETER_NOT_VALID);
      response.setErrorMessage(e.getMessage());
    }
    return response;
  }

  @Override
  public ServiceResponse unscheduleJob(String jobName, String jobGroup) {
    ServiceResponse response = ServiceResponse.newSuccessServiceResponse();
    try {
      if (!checkExists(jobName, jobGroup)) {
        response.setSuccess(false);
        response.setErrorCode(ErrorCode.ERROR_PARAMETER_NOT_VALID);
        response.setErrorMessage("job does not exists.");
        return response;
      }
      if (!scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroup))) {
        response.setSuccess(false);
        response.setErrorCode(ErrorCode.ERROR_PARAMETER_NOT_VALID);
        response.setErrorMessage("job trigger does not exists.");
        return response;
      }
    } catch (SchedulerException e) {
      LOGGER.error("", e);
      response.setSuccess(false);
      response.setErrorCode(ErrorCode.ERROR_PARAMETER_NOT_VALID);
      response.setErrorMessage(e.getMessage());
    }
    return response;
  }

  @Override
  public boolean checkScheduled(String jobName, String jobGroup) {
    try {
      if (!checkExists(jobName, jobGroup)) {
        return false;
      }
      return scheduler.getTrigger(TriggerKey.triggerKey(jobName, jobGroup)) == null;
    } catch (SchedulerException e) {
      LOGGER.error("", e);
    }
    return false;
  }

  @Override
  public ServiceDataResponse<JobMeta, Void> getJobMeta(String jobName, String jobGroup) {
    ServiceDataResponse<JobMeta, Void> response = ServiceDataResponse.newSuccessServiceResponse();
    try {
      JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroup));
      response.setResult(QuartzUtil.jobDetail2JobMeta(jobDetail));
    } catch (SchedulerException e) {
      LOGGER.error("", e);
      response.setSuccess(false);
      response.setErrorCode(ErrorCode.ERROR_PARAMETER_NOT_VALID);
      response.setErrorMessage(e.getMessage());
    }
    return response;
  }

  @Override
  public boolean checkExists(String jobName, String jobGroup) {
    try {
      return scheduler.checkExists(JobKey.jobKey(jobName, jobGroup));
    } catch (SchedulerException e) {
      LOGGER.error("", e);
    }
    return false;
  }

  @Override
  public ServiceDataResponse<List<JobMeta>, Void> getAllJobs() {
    ServiceDataResponse<List<JobMeta>, Void> response = ServiceDataResponse.newSuccessServiceResponse();
    try {
      Set<JobKey> jobKeySet = scheduler.getJobKeys(GroupMatcher.anyGroup());
      List<JobMeta> result = new ArrayList<>(jobKeySet.size());
      jobKeySet.forEach(key -> {
        try {
          result.add(QuartzUtil.jobDetail2JobMeta(scheduler.getJobDetail(key)));
        } catch (SchedulerException e) {
          LOGGER.error("", e);
        }
      });
      response.setResult(result);
    } catch (SchedulerException e) {
      LOGGER.error("", e);
      response.setSuccess(false);
      response.setErrorCode(ErrorCode.ERROR_PARAMETER_NOT_VALID);
      response.setErrorMessage(e.getMessage());
    }
    return response;
  }
}
