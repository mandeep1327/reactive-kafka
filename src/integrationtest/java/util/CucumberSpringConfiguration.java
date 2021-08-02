package util;

import io.cucumber.spring.CucumberContextConfiguration;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.DcsaEventProcessorApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = DcsaEventProcessorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(value = "integrationtest")
public class CucumberSpringConfiguration {
}
