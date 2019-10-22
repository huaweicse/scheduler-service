package org.apache.servicecomb.scheduler.client;

import org.apache.servicecomb.provider.pojo.RpcSchema;
import org.apache.servicecomb.scheduler.common.AbstractClientJob;
import org.apache.servicecomb.scheduler.common.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RpcSchema(schemaId = "LongJob")
public class LongJob extends AbstractClientJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(LongJob.class);

    @Override
    protected void run(JobContext jobContext) {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("LongJob executed successfully.");
    }
}
