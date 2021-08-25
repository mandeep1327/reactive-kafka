package net.apmoller.crb.microservices.external.apis.dcsa.processor;

import net.apmoller.crb.microservices.external.apis.dcsa.processor.service.GEMSPubTypeKafkaConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DcsaEventProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DcsaEventProcessorApplication.class, args);
    }
}
