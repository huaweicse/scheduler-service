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
package org.apache.servicecomb.scheduler.server.management;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.scheduler.common.JobContext;
import org.apache.servicecomb.scheduler.common.JobMeta;
import org.apache.servicecomb.scheduler.common.ServiceDataResponse;
import org.apache.servicecomb.scheduler.common.ServiceResponse;
import org.apache.servicecomb.scheduler.server.engine.ExecutionEngine;
import org.apache.servicecomb.scheduler.server.engine.SchedulerEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestSchema(schemaId = "ManagementEndpoint")
@RequestMapping(path = "/manage")
public class ManagementEndpoint {
  @Autowired
  @Qualifier("ExecutionEngine")
  private ExecutionEngine executionEngine;

  @Autowired
  SchedulerEngine schedulerEngine;

  @PostMapping(path = "/createJob")
  public ServiceResponse createJob(@RequestBody JobMeta jobMeta) {
    return schedulerEngine.createJob(jobMeta);
  }

  @GetMapping(path = "/scheduleJob")
  public ServiceResponse scheduleJob(@RequestParam("jobName") String jobName,
      @RequestParam("jobGroup") String jobGroup) {
    return schedulerEngine.scheduleJob(jobName, jobGroup, executionEngine);
  }

  @DeleteMapping(path = "/unscheduleJob")
  public ServiceResponse unscheduleJob(@RequestParam("jobName") String jobName,
      @RequestParam("jobGroup") String jobGroup) {
    return schedulerEngine.unscheduleJob(jobName, jobGroup);
  }

  @GetMapping(path = "/executeJob")
  public ServiceResponse executeJob(@RequestParam("jobName") String jobName,
      @RequestParam("jobGroup") String jobGroup) {
    ServiceDataResponse<JobMeta, Void> response = schedulerEngine.getJobMeta(jobName, jobGroup);
    if (response.isSuccess()) {
      executionEngine.trigger(response.getResult());
    }
    return response;
  }

  @GetMapping(path = "/runningJobs")
  public List<JobContext> runningJobs() {
    return executionEngine.getRunningJobs();
  }

  @GetMapping(path = "/getAllJobs")
  public ServiceDataResponse<List<JobMeta>, Void> getAllJobs() {
    return schedulerEngine.getAllJobs();
  }

  @GetMapping(path = "/logs")
  public File logs() {
    File current = new File(System.getProperty("user.dir"));
    File files[] = current.listFiles((f) -> {
      return f.isFile() && f.getName().startsWith("jobs");
    });
    Arrays.sort(files, (a, b) -> {
      return b.getName().compareTo(a.getName());
    });
    return files[0];
  }
}
