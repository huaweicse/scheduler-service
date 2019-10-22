package com.anta.servicecomb.edgeservice;

import org.apache.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@EnableServiceComb
public class EdgeServiceApplication {
    public static void main(String[] args) throws Exception {
        try {
            new SpringApplicationBuilder(EdgeServiceApplication.class).web(false).build().run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
