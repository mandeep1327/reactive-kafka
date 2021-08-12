package net.apmoller.crb.microservices.external.apis.dcsa.processor.mappers;

import com.maersk.jaxb.pojo.GEMSPubType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EquipmentEventMapperImpl;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.DocumentReference;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;

import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.DocumentReference.Key.BKG;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.DocumentReference.Key.TRD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent.EquipmentEventType.GTIN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent.EquipmentEventType.GTOT;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall.FacilityTypeCode.CLOC;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getGemsData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithARRIVECUIMPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithDEPARTCUEXPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForTransportEventType;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EquipmentEventMapperTest {

    private final static Event baseEventData = getEventForTransportEventType() ;
    private EquipmentEventMapperImpl equipmentEventMapper = new EquipmentEventMapperImpl();

    @ParameterizedTest
    @MethodSource("createEquipmentEventTestData")
    public void testEquipmentDataForLegitScenarios(GEMSPubType gemsData, Event expectedShipmentEvent) {
        var pubSetData = gemsData.getPubSet().get(0);
        var actualEquipmentEvent = equipmentEventMapper.fromPubSetToEquipmentEvent(pubSetData, baseEventData);
        assertEquals(expectedShipmentEvent, actualEquipmentEvent, "Equipment Event does not match");
    }

    private static Stream<Arguments> createEquipmentEventTestData() {
        return Stream.of(
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithARRIVECUIMPNEventAct())), getEquipmentEventTestData(GTIN)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithDEPARTCUEXPNEventAct())), getEquipmentEventTestData(GTOT))
        );
    }

    private static TransportCall getTransportCall(TransportCall.FacilityTypeCode facilityCode, TransportCall.TransPortMode transPortMode) {
        return TransportCall.builder()
                .facilityTypeCode(facilityCode)
                .carrierServiceCode("LineCode")
                .carrierVoyageNumber(null)
                .otherFacility("Copenhagen")
                .modeOfTransport(transPortMode)
                .build();
    }

    private static EquipmentEvent getEquipmentEventTestData(EquipmentEvent.EquipmentEventType eventType) {
        return EquipmentEvent.builder()
                .equipmentEventType(eventType)
                .emptyIndicatorCode(EquipmentEvent.EmptyIndicatorCode.LADEN)
                .documentReferences(getDocumentRef())
                .seals(new ArrayList<>())
                .eventID(baseEventData.getEventID())
                .transportCall(getTransportCall(CLOC, null))
                .bookingReference(baseEventData.getBookingReference())
                .eventDateTime(baseEventData.getEventDateTime())
                .eventType(baseEventData.getEventType())
                .eventCreatedDateTime(baseEventData.getEventCreatedDateTime())
                .eventClassifierCode(baseEventData.getEventClassifierCode())
                .parties(baseEventData.getParties())
                .references(baseEventData.getReferences())
                .equipmentReference(baseEventData.getEquipmentReference())
                .carrierBookingReference(baseEventData.getCarrierBookingReference())
                .transportDocumentReference(baseEventData.getTransportDocumentReference())
                .sourceSystem(baseEventData.getSourceSystem())
                .serviceType(baseEventData.getServiceType())
                .carrierCode(baseEventData.getCarrierCode())
                .build();
    }

    private static List<DocumentReference> getDocumentRef() {
        return List.of(getDocRefBkg(), getDocRefTrd());
    }

    private static DocumentReference getDocRefTrd() {
        return DocumentReference.builder()
                .key(TRD)
                .value("293156737")
                .build();
    }

    private static DocumentReference getDocRefBkg() {
        return DocumentReference.builder()
                .key(BKG)
                .value("209989099")
                .build();
    }
}
