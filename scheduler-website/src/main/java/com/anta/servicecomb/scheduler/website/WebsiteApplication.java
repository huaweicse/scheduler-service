package com.anta.servicecomb.scheduler.website;

import org.apache.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableServiceComb
public class WebsiteApplication {
  public static void main(String[] args) throws Exception {
    SpringApplication.run(WebsiteApplication.class, args);
  }
}
