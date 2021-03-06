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
import org.apache.servicecomb.core.SCBEngine;
import org.apache.servicecomb.core.definition.SchemaMeta;
import org.apache.servicecomb.core.invocation.InvocationFactory;
import org.apache.servicecomb.core.provider.consumer.ReferenceConfig;
import org.apache.servicecomb.serviceregistry.definition.DefinitionConst;
import org.apache.servicecomb.serviceregistry.discovery.DiscoveryTree;

import java.util.concurrent.CompletableFuture;

public abstract class Task implements Runnable {
    protected SchedulerContext contextInternal;
    protected CompletableFuture<SchedulerContext> callback;
    protected DiscoveryTree discoveryTree;

    public Task(SchedulerContext contextInternal, CompletableFuture<SchedulerContext> callback, DiscoveryTree discoveryTree) {
        this.contextInternal = contextInternal;
        this.callback = callback;
        this.discoveryTree = discoveryTree;
    }

    protected static Invocation generateInvocation(String microserviceName, String schemaId, String operationName, Object[] args) {
        ReferenceConfig referenceConfig = SCBEngine.getInstance().createReferenceConfigForInvoke(microserviceName,
                DefinitionConst.VERSION_RULE_ALL,
                "");
        SchemaMeta schemaMeta = referenceConfig.getMicroserviceMeta().ensureFindSchemaMeta(schemaId);
        return InvocationFactory.forConsumer(referenceConfig, schemaMeta, operationName, args);
    }

    @Override
    public void run() {
        callback.complete(doRun());
    }

    abstract SchedulerContext doRun();
}
