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

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractClientJob implements Job {
    @Autowired
    private ClientJobExecutor executor;

    @Override
    public JobContext start(JobContext context) {
        return executor.executeJob(context, this);
    }

    @ApiOperation(value = "", hidden = true)
    protected abstract void run(JobContext jobContext);

    @Override
    public JobContext poll(JobContext context) {
        return executor.pollJob(context, this);
    }
}
