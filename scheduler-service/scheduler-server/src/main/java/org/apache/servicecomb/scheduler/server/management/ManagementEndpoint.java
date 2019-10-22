package org.apache.servicecomb.scheduler.server.management;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestSchema(schemaId = "ManagementEndpoint")
@RequestMapping(path = "/manage")
public class ManagementEndpoint {
    @Autowired
    @Qualifier("ExecutionEngine")
    private ExecutionEngine executionEngine;

    @Autowired
    SchedulerEngine schedulerEngine;

    @Autowired
    private List<JobMeta> jobMetaList;

    @PostMapping(path = "/triggerJob")
    public boolean triggerJob(@RequestBody JobMeta jobMeta) {
        executionEngine.trigger(jobMeta);
        return true;
    }

    @PostMapping(path = "/stopJob")
    public boolean stopJob(@RequestBody JobMeta jobMeta) {
        for (JobMeta m : jobMetaList) {
            if (m.equals(jobMeta)) {
                return schedulerEngine.stopJob(jobMeta);
            }
        }
        return false;
    }

    @PostMapping(path = "/startJob")
    public boolean startJob(@RequestBody JobMeta jobMeta) {
        for (JobMeta m : jobMetaList) {
            if (m.equals(jobMeta)) {
                return schedulerEngine.scheduleJob(m, executionEngine);
            }
        }
        return false;
    }

    @GetMapping(path = "/runningJobs")
    public List<JobContext> runningJobs() {
        return executionEngine.getRunningJobs();
    }

    @GetMapping(path = "/getSystemJobs")
    public List<JobMeta> getSystemJobs() {
        List<JobMeta> jobs = new ArrayList<>(jobMetaList.size());
        jobMetaList.forEach(item -> {
            JobMeta m = new JobMeta(item.getJobName(), item.getGroupName());
            m.addProperty(JobMeta.PROPERTY_CRON, item.getProperty(JobMeta.PROPERTY_CRON));
            m.addProperty(JobMeta.PROPERTY_DESC, item.getProperty(JobMeta.PROPERTY_DESC));
            if (schedulerEngine.checkExists(m)) {
                m.addProperty(JobMeta.PROPERTY_EXISTS, Boolean.TRUE.toString());
            } else {
                m.addProperty(JobMeta.PROPERTY_EXISTS, Boolean.FALSE.toString());
            }
            jobs.add(m);
        });
        return jobs;
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
