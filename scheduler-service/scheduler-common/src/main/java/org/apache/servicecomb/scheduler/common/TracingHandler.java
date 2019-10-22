package org.apache.servicecomb.scheduler.common;


import org.apache.servicecomb.core.Handler;
import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.swagger.invocation.AsyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TracingHandler implements Handler {
    private static final String TRACING_KEY = "anta_tracing_key";
    private static final Logger LOGGER = LoggerFactory.getLogger(TracingHandler.class);

    @Override
    public void handle(Invocation invocation, AsyncResponse asyncResponse) throws Exception {
        String id = invocation.getContext(TRACING_KEY);
        if (id == null) {
            id = UUID.randomUUID().toString();
            invocation.addContext(TRACING_KEY, id);
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
