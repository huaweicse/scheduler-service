package org.apache.servicecomb.scheduler.server;

import org.apache.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@EnableServiceComb
public class SchedulerServer {
    public static void main(String[] args) throws Exception {
        try {
            new SpringApplicationBuilder(SchedulerServer.class).web(false).build().run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
