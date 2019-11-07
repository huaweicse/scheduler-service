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
package org.apache.servicecomb.scheduler.server;

import org.apache.servicecomb.core.BootListener;
import org.apache.servicecomb.scheduler.common.JobMeta;
import org.apache.servicecomb.scheduler.server.engine.ExecutionEngine;
import org.apache.servicecomb.scheduler.server.engine.SchedulerEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobInitializer implements BootListener {
    private SchedulerEngine schedulerEngine;
    private ExecutionEngine executionEngine;
    private List<JobMeta> jobMetaList;

    public JobInitializer(@Autowired SchedulerEngine schedulerEngine, @Autowired ExecutionEngine executionEngine
            , @Autowired List<JobMeta> jobMetaList) {
        this.schedulerEngine = schedulerEngine;
        this.executionEngine = executionEngine;
        this.jobMetaList = jobMetaList;
    }

    @Override
    public void onBootEvent(BootEvent bootEvent) {
        // 根据当前应用场景， 启动的时候不注册服务
//        if (bootEvent.getEventType().equals(EventType.AFTER_REGISTRY)) {
//            for (JobMeta meta : jobMetaList) {
//                schedulerEngine.scheduleJob(meta, executionEngine);
//            }
//        }
    }
}
