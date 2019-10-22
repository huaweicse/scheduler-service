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
