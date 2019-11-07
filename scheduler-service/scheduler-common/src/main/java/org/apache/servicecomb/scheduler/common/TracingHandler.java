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

import org.apache.servicecomb.core.Const;
import org.apache.servicecomb.core.Handler;
import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.core.tracing.BraveTraceIdGenerator;
import org.apache.servicecomb.core.tracing.TraceIdGenerator;
import org.apache.servicecomb.swagger.invocation.AsyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TracingHandler implements Handler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TracingHandler.class);
    private static final TraceIdGenerator generator = new BraveTraceIdGenerator();

    @Override
    public void handle(Invocation invocation, AsyncResponse asyncResponse) throws Exception {
        String id = invocation.getContext(Const.TRACE_ID_NAME);
        if (id == null) {
            id = generator.generate();
            invocation.addContext(Const.TRACE_ID_NAME, id);
        }
        final String traceId = id;

        long time = System.currentTimeMillis();
        if (invocation.isConsumer()) {
            LOGGER.info("{} {} start send request.", traceId, invocation.getMicroserviceQualifiedName());
        } else {
            LOGGER.info("{} {} start recv request.", traceId, invocation.getMicroserviceQualifiedName());
        }

        invocation.next((response) -> {
            if (invocation.isConsumer()) {
                LOGGER.info("{} {} end send request, success {}, token {}, ip {}.",
                        traceId,
                        invocation.getMicroserviceQualifiedName(),
                        response.isSuccessed(),
                        System.currentTimeMillis() - time,
                        invocation.getEndpoint().getEndpoint());
            } else {
                LOGGER.info("{} {} end recv request, success {}, token {}.",
                        traceId,
                        invocation.getMicroserviceQualifiedName(),
                        response.isSuccessed(),
                        System.currentTimeMillis() - time);
            }
            asyncResponse.complete(response);
        });
    }
}
