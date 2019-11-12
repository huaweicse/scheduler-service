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

import org.apache.servicecomb.scheduler.common.JobMeta;
import org.apache.servicecomb.scheduler.server.engine.ExecutionEngine;
import org.apache.servicecomb.scheduler.server.engine.SchedulerEngine;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
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
  public boolean createJob(JobMeta jobMeta) {
    try {
      JobDetail jobDetail = QuartzUtil.jobMeta2JobDetail(jobMeta);
      scheduler.addJob(jobDetail, true);
      return true;
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean scheduleJob(JobMeta jobMeta, ExecutionEngine engine) {
    try {
      if(checkExists(jobMeta)) {
        scheduler.deleteJob(JobKey.jobKey(jobMeta.getJobName(), jobMeta.getGroupName()));
      }
      JobDetail jobDetail = QuartzUtil.jobMeta2JobDetail(jobMeta);
      Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobMeta.getJobName(), jobMeta.getGroupName())
          .withSchedule(CronScheduleBuilder.cronSchedule(jobMeta.getProperty(JobMeta.PROPERTY_CRON))).build();
      scheduler.scheduleJob(jobDetail, trigger);
      return true;
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean unscheduleJob(JobMeta jobMeta) {
    try {
      if(!checkExists(jobMeta)) {
        return true;
      }
      return scheduler.unscheduleJob(TriggerKey.triggerKey(jobMeta.getJobName(), jobMeta.getGroupName()));
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean checkScheduled(JobMeta jobMeta) {
    try {
      if(!checkExists(jobMeta)) {
        return false;
      }
      return scheduler.getTrigger(TriggerKey.triggerKey(jobMeta.getJobName(), jobMeta.getGroupName())) == null;
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean checkExists(JobMeta meta) {
    try {
      return scheduler.checkExists(JobKey.jobKey(meta.getJobName(), meta.getGroupName()));
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public List<JobMeta> getAllJobs() {
    try {
      Set<JobKey> jobKeySet = scheduler.getJobKeys(GroupMatcher.anyGroup());
      List<JobMeta> result = new ArrayList<>(jobKeySet.size());
      jobKeySet.forEach(key -> {
        try {
          result.add(QuartzUtil.jobDetail2JobMeta(scheduler.getJobDetail(key)));
        } catch (SchedulerException e) {
          e.printStackTrace();
        }
      });
      return result;
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
    return null;
  }
}
