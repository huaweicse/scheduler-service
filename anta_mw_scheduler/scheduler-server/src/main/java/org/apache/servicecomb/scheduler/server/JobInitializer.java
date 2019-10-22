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
