package net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders;

import MSK.com.gems.EndLocType;
import MSK.com.gems.EventType;
import MSK.com.gems.GEMSPubType;
import MSK.com.gems.GTTSVesselType;
import MSK.com.gems.PartyType;
import MSK.com.gems.PubSetType;
import MSK.com.gems.StartLocType;
import MSK.com.gems.TransportPlanType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.EquipmentTestDataBuilder.getEquipmentList;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.ShipmentTestDataBuilder.getPartyList;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.ShipmentTestDataBuilder.getPartyList2;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.ShipmentTestDataBuilder.getPartyList3;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.ShipmentTestDataBuilder.getPartyList4;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.ShipmentTestDataBuilder.getShipmentValue;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.TPDocTypeTestDataBuilder.getTPDocList;

public final class GEMSPubTestDataBuilder {

    private GEMSPubTestDataBuilder(){}

    public static GEMSPubType getGemsData(List<PubSetType> pubSetList){

        var gemsPubType = new GEMSPubType();
        gemsPubType.setPubSet(pubSetList);
        return gemsPubType;

    }

    public static PubSetType getPubSet(EventType eventType) {

        var pubSetType = new PubSetType();
        pubSetType.setEvent(eventType);
        pubSetType.setShipment(getShipmentValue(getPartyList()));
        pubSetType.setEquipment(getEquipmentList());
        pubSetType.setTpdoc(getTPDocList());
        return pubSetType;

    }

    public static PubSetType getPubSet(EventType eventType, List<PartyType> party) {

        var pubSetType = new PubSetType();
        pubSetType.setEvent(eventType);
        pubSetType.setShipment(getShipmentValue(party));
        pubSetType.setEquipment(getEquipmentList());
        pubSetType.setTpdoc(getTPDocList());
        return pubSetType;

    }


    public static PubSetType getPubSetWithTransportPlan(EventType eventType, List<PartyType> parties) {

        PubSetType pubSetType = getCommonPubSetTypeWithVesselData(eventType, parties);
        pubSetType.setTransportPlan(List.of(getTransportPlan()));
        return pubSetType;

    }

    private static PubSetType getPubSetForDepartureWithTransportPlan(EventType eventType, List<PartyType> parties) {
        var pubSetType = getPubSetWithTransportPlan(eventType, parties);
        pubSetType.setTransportPlan(List.of(getTransportPlanForDeparture("MVS")));
        return pubSetType;
    }

    private static PubSetType getPubSetForDepartureWithTransportPlanAndFEFTransportMode(EventType eventType, List<PartyType> parties) {
        var pubSetType = getPubSetWithTransportPlan(eventType, parties);
        pubSetType.setTransportPlan(List.of(getTransportPlanForDeparture("FEF")));
        return pubSetType;
    }

    private static PubSetType getPubSetForDepartureWithTransportPlanAndVSFTransportMode(EventType eventType, List<PartyType> parties) {
        var pubSetType = getPubSetWithTransportPlan(eventType, parties);
        pubSetType.setTransportPlan(List.of(getTransportPlanForDeparture("VSF")));
        return pubSetType;
    }

    private static PubSetType getPubSetForDepartureWithTransportPlanAndFEOTransportMode(EventType eventType, List<PartyType> parties) {
        var pubSetType = getPubSetWithTransportPlan(eventType, parties);
        pubSetType.setTransportPlan(List.of(getTransportPlanForDeparture("FEO")));
        return pubSetType;
    }

    private static PubSetType getPubSetForDepartureWithTransportPlanAndVSMTransportMode(EventType eventType, List<PartyType> parties) {
        var pubSetType = getPubSetWithTransportPlan(eventType, parties);
        pubSetType.setTransportPlan(List.of(getTransportPlanForDeparture("VSM")));
        return pubSetType;
    }

    private static PubSetType getPubSetForDepartureWithTransportPlanAndVSLTransportMode(EventType eventType, List<PartyType> parties) {
        var pubSetType = getPubSetWithTransportPlan(eventType, parties);
        pubSetType.setTransportPlan(List.of(getTransportPlanForDeparture("VSL")));
        return pubSetType;
    }


    private static PubSetType getPubSetForDepartureWithTransportPlanAndRCOTransportMode(EventType eventType, List<PartyType> parties) {
        var pubSetType = getPubSetWithTransportPlan(eventType, parties);
        pubSetType.setTransportPlan(List.of(getTransportPlanForDeparture("RCO")));
        return pubSetType;
    }

    private static PubSetType getPubSetForDepartureWithTransportPlanAndTRKTransportMode(EventType eventType, List<PartyType> parties) {
        var pubSetType = getPubSetWithTransportPlan(eventType, parties);
        pubSetType.setTransportPlan(List.of(getTransportPlanForDeparture("TRK")));
        return pubSetType;
    }

    private static PubSetType getPubSetForDepartureWithTransportPlanAndBARTransportMode(EventType eventType, List<PartyType> parties) {
        var pubSetType = getPubSetWithTransportPlan(eventType, parties);
        pubSetType.setTransportPlan(List.of(getTransportPlanForDeparture("BAR")));
        return pubSetType;
    }


    public static PubSetType getCommonPubSetTypeWithVesselData(EventType eventType, List<PartyType> parties) {
        var pubSetType = getPubSet(eventType, parties);
        pubSetType.setGttsvessel(getVesselData());
        return pubSetType;
    }


    private static TransportPlanType getTransportPlan() {
        TransportPlanType transportPlan = getCommonTransportPlanType(getStartLoc(), getEndLoc());
        transportPlan.setTransMode("MVS");
        return transportPlan;
    }

    private static TransportPlanType getTransportPlanForDeparture(String transportMode) {
        TransportPlanType transportPlan = getCommonTransportPlanType(getStartLocWithCopenhagen(), getEndLocWithKolkata());
        transportPlan.setTransMode(transportMode);
        return transportPlan;
    }

    @NotNull
    private static TransportPlanType getCommonTransportPlanType(StartLocType startLocWithCopenhagen, EndLocType endLocWithKolkata) {
        var transportPlan = new TransportPlanType();
        transportPlan.setStartLoc(startLocWithCopenhagen);
        transportPlan.setEndLoc(endLocWithKolkata);
        transportPlan.setVesselCde("MUMMRSK");
        return transportPlan;
    }

    private static StartLocType getStartLoc() {
        var startLocation = new StartLocType();
        startLocation.setValue("Kolkata");
        return startLocation;
    }

    private static StartLocType getStartLocWithCopenhagen() {
        var startLocation = new StartLocType();
        startLocation.setValue("Copenhagen");
        return startLocation;
    }

    private static EndLocType getEndLoc() {
        var endLocation = new EndLocType();
        endLocation.setValue("Copenhagen");
        return endLocation;
    }

    private static EndLocType getEndLocWithKolkata() {
        var endLocation = new EndLocType();
        endLocation.setValue("Kolkata");
        return endLocation;
    }

    private static GTTSVesselType getVesselData() {
        var vesselData = new GTTSVesselType();
        vesselData.setGttsdte("2021-21-21 ");
        vesselData.setGttstim("13:29");
        return vesselData;
    }

    public static PubSetType getPubSetTypeWithArrange_Cargo_Release_ClosedEventAct(){
        return getPubSet(getEventTypeData("Arrange_Cargo_Release_Closed"));
    }
    public static PubSetType getPubSetTypeWithArrange_Cargo_Release_OpenEventAct(){
        return getPubSet(getEventTypeData("Arrange_Cargo_Release_Open"));
    }
    public static PubSetType getPubSetTypeWithARRIVAL_NOTICEEventAct(){
        return getPubSet(getEventTypeData("ARRIVAL_NOTICE"));
    }
    public static PubSetType getPubSetTypeWithConfirm_Shipment_ClosedEventAct(){
        return getPubSet(getEventTypeData("Confirm_Shipment_Closed"));
    }
    public static PubSetType getPubSetTypeWithEquipment_VGM_Details_UpdatedEventAct(){
        return getPubSet(getEventTypeData("Equipment_VGM_Details_Updated"));
    }
    public static PubSetType getPubSetTypeWithIssue_Original_TPDOC_ClosedEventAct(){
        return getPubSet(getEventTypeData("Issue_Original_TPDOC_Closed"));
    }
    public static PubSetType getPubSetTypeWithReceive_Transport_Document_Instructions_ClosedEventAct(){
        return getPubSet(getEventTypeData("Receive_Transport_Document_Instructions_Closed"));
    }
    public static PubSetType getPubSetTypeWithRELEASEEventAct(){
        return getPubSet(getEventTypeData("RELEASE"));
    }
    public static PubSetType getPubSetTypeWithShipment_CancelledEventAct(){
        return getPubSet(getEventTypeData("Shipment_Cancelled"));
    }
    public static PubSetType getPubSetTypeWithDemoEventAct(){
        return getPubSet(getEventTypeData("NA_Event_Act"));
    }
    public static PubSetType getPubSetTypeWithARRIVECUIMPNEventAct(){
        return getPubSetWithTransportPlan(getEventTypeData("ARRIVECUIMPN"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithDEPARTCUEXPNEventAct(){
        return getPubSetForDepartureWithTransportPlanAndFEFTransportMode(getEventTypeData("DEPARTCUEXPN"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithDISCHARG_NEventAct(){
        return getPubSet(getEventTypeData("DISCHARG   N"));
    }
    public static PubSetType getPubSetTypeWithGATE_IN_EXPNEventAct(){
        return getPubSet(getEventTypeData("GATE-IN EXPN"));
    }
    public static PubSetType getPubSetTypeWithGATE_OUTEXPYEventAct(){
        return getPubSet(getEventTypeData("GATE-OUTEXPY"));
    }
    public static PubSetType getPubSetTypeWithLOAD_NEventAct(){
        return getPubSet(getEventTypeData("LOAD       N"));
    }
    public static PubSetType getPubSetTypeWithOFF_RAILIMPNEventAct(){
        return getPubSet(getEventTypeData("OFF-RAILIMPN"));
    }
    public static PubSetType getPubSetTypeWithON_RAIL_EXPNEventAct(){
        return getPubSet(getEventTypeData("ON-RAIL EXPN"));
    }
    public static PubSetType getPubSetTypeWithSTRIPPIN_YEventAct(){
        return getPubSet(getEventTypeData("STRIPPIN   Y"));
    }
    public static PubSetType getPubSetTypeWithSTUFFINGEXPNEventAct(){
        return getPubSet(getEventTypeData("STUFFINGEXPN"));
    }

    public static PubSetType getPubSetTypeWithCONTAINER_ARRIVALEventAct(){
        return getPubSetWithTransportPlan(getEventTypeData("CONTAINER ARRIVAL"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithCONTAINER_DEPARTUREEventAct(){
        return getPubSetForDepartureWithTransportPlan(getEventTypeData("CONTAINER DEPARTURE"), getPartyList2());
    }

    public static PubSetType getPubSetTypeWithRAIL_ARRIVAL_AT_DESTINATIONEventAct(){
        return getPubSetWithTransportPlan(getEventTypeData("RAIL_ARRIVAL_AT_DESTINATION"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithRAIL_DEPARTUREEventAct(){
        return getPubSetForDepartureWithTransportPlan(getEventTypeData("RAIL_DEPARTURE"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithShipment_ETAEventAct(){
        return getPubSetWithTransportPlan(getEventTypeData("Shipment_ETA"), getPartyList3());
    }
    public static PubSetType getPubSetTypeWithShipment_ETDEventAct(){
        return getPubSetForDepartureWithTransportPlan(getEventTypeData("Shipment_ETD"), getPartyList4());
    }
    public static PubSetType getPubSetTypeWithShipment_ETDEventActAndFEFTransportMode(){
        return getPubSetForDepartureWithTransportPlanAndFEFTransportMode(getEventTypeData("Shipment_ETD"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithRAIL_DEPARTUREEventActAndRCOTransportMode(){
        return getPubSetForDepartureWithTransportPlanAndRCOTransportMode(getEventTypeData("RAIL_DEPARTURE"), getPartyList2());
    }

    public static PubSetType getPubSetTypeWithCONTAINER_DEPARTUREEventActAndRCOTransportMode(){
        return getPubSetForDepartureWithTransportPlanAndTRKTransportMode(getEventTypeData("CONTAINER DEPARTURE"), getPartyList2());
    }

    public static PubSetType getPubSetTypeWithCONTAINER_DEPARTUREEventActAndBARTransportMode(){
        return getPubSetForDepartureWithTransportPlanAndBARTransportMode(getEventTypeData("CONTAINER DEPARTURE"), getPartyList2());
    }

    public static PubSetType getPubSetTypeWithShipment_ETDEventActAndVSFTransportMode(){
        return getPubSetForDepartureWithTransportPlanAndVSFTransportMode(getEventTypeData("Shipment_ETD"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithShipment_ETDEventActAndFEOTransportMode(){
        return getPubSetForDepartureWithTransportPlanAndFEOTransportMode(getEventTypeData("Shipment_ETD"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithShipment_ETDEventActAndVSMTransportMode(){
        return getPubSetForDepartureWithTransportPlanAndVSMTransportMode(getEventTypeData("Shipment_ETD"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithShipment_ETDEventActAndVSLTransportMode(){
        return getPubSetForDepartureWithTransportPlanAndVSLTransportMode(getEventTypeData("Shipment_ETD"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithShipment_ETDEventActAndRCOTransportMode(){
        return getPubSetForDepartureWithTransportPlanAndRCOTransportMode(getEventTypeData("Shipment_ETD"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithoutTransportPlan(){
        return getCommonPubSetTypeWithVesselData(getEventTypeData("Shipment_ETD"), getPartyList2());
    }
    public static PubSetType getPubSetTypeWithoutVesselData(){
        var pubSetData= getPubSet(getEventTypeData("Shipment_ETD"));
        pubSetData.setTransportPlan(List.of(getTransportPlan()));
        return pubSetData;
    }

    private static EventType getEventTypeData(String eventAct) {

        var eventType = new EventType();
        eventType.setEventId(2537152461542365L);
        eventType.setEventAct(eventAct);
        eventType.setGemstsutc("2021-09-23 23:45");
        eventType.setSrcSys("GCSS");
        eventType.setSrcSysTimestamp("2021-09-23 23:45");
        eventType.setRkemmove("DISCHARG   N");
        return eventType;

    }


}