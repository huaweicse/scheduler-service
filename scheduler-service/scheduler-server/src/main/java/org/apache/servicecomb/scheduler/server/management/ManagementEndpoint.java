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
import org.apache.servicecomb.scheduler.server.engine.ExecutionEngine;
import org.apache.servicecomb.scheduler.server.engine.SchedulerEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestSchema(schemaId = "ManagementEndpoint")
@RequestMapping(path = "/manage")
public class ManagementEndpoint {
  @Autowired
  @Qualifier("ExecutionEngine")
  private ExecutionEngine executionEngine;

  @Autowired
  SchedulerEngine schedulerEngine;

  @PostMapping(path = "/triggerJob")
  public boolean triggerJob(@RequestBody JobMeta jobMeta) {
    executionEngine.trigger(jobMeta);
    return true;
  }

  @PostMapping(path = "/createJob")
  public boolean createJob(@RequestBody JobMeta jobMeta) {
    return schedulerEngine.createJob(jobMeta);
  }

  @PostMapping(path = "/unscheduleJob")
  public boolean unscheduleJob(@RequestBody JobMeta jobMeta) {
    return schedulerEngine.unscheduleJob(jobMeta);
  }

  @PostMapping(path = "/startJob")
  public boolean startJob(@RequestBody JobMeta jobMeta) {
    return schedulerEngine.scheduleJob(jobMeta, executionEngine);
  }

  @GetMapping(path = "/runningJobs")
  public List<JobContext> runningJobs() {
    return executionEngine.getRunningJobs();
  }

  @GetMapping(path = "/getAllJobs")
  public List<JobMeta> getAllJobs() {
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
