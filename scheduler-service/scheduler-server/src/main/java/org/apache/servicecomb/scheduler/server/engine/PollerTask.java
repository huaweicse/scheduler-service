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

import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.core.provider.consumer.InvokerUtils;
import org.apache.servicecomb.loadbalance.LoadbalanceHandler;
import org.apache.servicecomb.scheduler.common.Job;
import org.apache.servicecomb.scheduler.common.JobContext;
import org.apache.servicecomb.serviceregistry.discovery.DiscoveryTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class PollerTask extends Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(PollerTask.class);

    public PollerTask(SchedulerContext contextInternal, CompletableFuture<SchedulerContext> callback, DiscoveryTree discoveryTree) {
        super(contextInternal, callback, discoveryTree);
    }


    @Override
    public SchedulerContext doRun() {
        try {
            Invocation invocation = generateInvocation(
                    contextInternal.getJobContext().getJobMeta().getGroupName(),
                    contextInternal.getJobContext().getJobMeta().getJobName(),
                    Job.METHOD_POLL,
                    new Object[]{contextInternal.getJobContext()});

            invocation.addLocalContext(LoadbalanceHandler.SERVICECOMB_SERVER_ENDPOINT, contextInternal.getCurrentEndpoint());

            JobContext result = (JobContext) InvokerUtils.syncInvoke(invocation);
            contextInternal.getJobContext().setJobStatus(result.getJobStatus());
            contextInternal.incrementSuccessPollCount();
        } catch (Exception e) {
            LOGGER.warn("polling error.", e);
            contextInternal.incrementFailedPollCount();
        }

        return contextInternal;
    }
}
