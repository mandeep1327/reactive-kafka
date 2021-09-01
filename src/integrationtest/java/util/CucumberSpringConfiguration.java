package util;

import io.cucumber.spring.CucumberContextConfiguration;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.DcsaEventProcessorApplication;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DCSAEventTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DocumentIdMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.EquipmentEventTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.EventClassifierCodeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ShipmentEventTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ShipmentInformationTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EquipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EquipmentEventMapperImpl;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EventMapperImpl;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.ShipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.ShipmentEventMapperImpl;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.TransportEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.TransportEventMapperImpl;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.service.EventDelegator;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.service.GEMSPubTypeKafkaConsumer;
import org.junit.ClassRule;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles(value = "integrationtest")
@Import(CucumberIntegrationIT.KafkaTestContainerConfiguration.class)
@CucumberContextConfiguration
@SpringBootTest(classes = {GEMSPubTypeKafkaConsumer.class, EventDelegator.class, EventMapperImpl.class,
         ShipmentEventMapperImpl.class, EquipmentEventMapperImpl.class, TransportEventMapperImpl.class,
        DCSAEventTypeMapper.class, EventClassifierCodeMapper.class, ShipmentEventTypeMapper.class,
        ShipmentInformationTypeMapper.class, DocumentIdMapper.class, EquipmentEventTypeMapper.class })
public class CucumberSpringConfiguration {

}
