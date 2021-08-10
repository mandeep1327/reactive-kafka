package net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders;

import com.maersk.jaxb.pojo.PartyType;
import com.maersk.jaxb.pojo.ShipmentType;

import java.util.List;

public final class ShipmentTestDataBuilder {

    private ShipmentTestDataBuilder(){}

    public static ShipmentType getShipmentValue() {
        return ShipmentType.newBuilder()
                .setParty(getPartyList())
                .setBookNo("209989099")
                .setRcvSvc("CY")
                .setDelSvc("CY")
                .setBreakBulk(null)
                .setCargoes(null)
                .setCargoTypes(null)
                .setEquipmentAssignments(null)
                .setHaulageArrangements(null)
                .setHistoryOfReasonsForTransportPlan(null)
                .setMilTStamp(null)
                .setOperationalRouteRef(null)
                .setOperator(null)
                .setPcfn(null)
                .setPublishSpotROTPNotification(null)
                .setReference(null)
                .setTpchangeReason(null)
                .setTpinfo(null)
                .build();
    }

    private static List<PartyType> getPartyList() {
        return List.of(getParty1(), getParty2());
    }

    private static PartyType getParty1() {
        return PartyType.newBuilder()
                .setCustRefNo(List.of("4351297464401500"))
                .setCustNo("406019090921")
                .setRoletyp("3")
                .setCustName("PARTY NAME 1")
                .setAddrLn1("DOREEN AVENUE")
                .setAddrLn2("SOUTH END")
                .setCity("ALASKA CITY")
                .setCntry("CANADA")
                .setPostCde("2430")
                .setAddrLn3(null)
                .setConcernCode(null)
                .setDodaac(null)
                .setState(null)
                .build();
    }


    private static PartyType getParty2() {
        return PartyType.newBuilder()
                .setCustRefNo(List.of("4351297464491500"))
                .setCustNo("241342126")
                .setRoletyp("4")
                .setCustName("PARTY NAME 2")
                .setAddrLn1("DOREEN AVENUE")
                .setAddrLn2("SOUTH END")
                .setCity("ALASKA CITY")
                .setCntry("CANADA")
                .setPostCde("2430")
                .setAddrLn3(null)
                .setConcernCode(null)
                .setDodaac(null)
                .setState(null)
                .build();
    }
}
