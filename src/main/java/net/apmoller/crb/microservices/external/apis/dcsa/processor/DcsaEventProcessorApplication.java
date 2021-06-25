package net.apmoller.crb.microservices.external.apis.dcsa.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DcsaEventProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DcsaEventProcessorApplication.class, args);
    }
}
