package org.apache.servicecomb.scheduler.client;

import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.apache.servicecomb.foundation.common.utils.Log4jUtils;
import org.apache.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@EnableServiceComb
public class SchedulerClient {
    public static void main(String[] args) throws Exception {
        try {
            new SpringApplicationBuilder(SchedulerClient.class).web(false).build().run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}