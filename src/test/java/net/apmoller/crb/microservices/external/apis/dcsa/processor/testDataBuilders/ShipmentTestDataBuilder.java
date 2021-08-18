package net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders;

import MSK.com.gems.PartyType;
import MSK.com.gems.ReferenceType;
import MSK.com.gems.ShipmentType;

import java.util.List;

public final class ShipmentTestDataBuilder {

    private ShipmentTestDataBuilder(){}

    public static ShipmentType getShipmentValue(List<PartyType> party) {
        var shipmentType = new ShipmentType();

        shipmentType.setParty(party);
        shipmentType.setBookNo("209989099");
        shipmentType.setRcvSvc("CY");
        shipmentType.setDelSvc("CY");
        shipmentType.setReference(getReferenceType41());

        return shipmentType;
    }

    public static List<PartyType> getPartyList() {
        return List.of(getParty1(), getParty2());
    }

    public static List<PartyType> getPartyList2() {
        return List.of(getParty2(), getParty3());
    }

    public static List<PartyType> getPartyList3() {
        return List.of(getParty3(), getParty4());
    }

    public static List<PartyType> getPartyList4() {
        return List.of(getParty4(), getParty1());
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

    private static PartyType getParty3() {
        return PartyType.newBuilder()
                .setCustRefNo(List.of("4351297464471500", "4351297464471501", "4351297464471502"))
                .setCustNo("80092546")
                .setRoletyp("15")
                .setCustName("PARTY NAME 3")
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

    private static PartyType getParty4() {
        return PartyType.newBuilder()
                .setCustRefNo(List.of("4351297464461500"))
                .setCustNo("26047945")
                .setRoletyp("1")
                .setCustName("PARTY NAME 4")
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

    private static List<ReferenceType> getReferenceType41() {
        return List.of(ReferenceType.newBuilder()
                .setTyp("41")
                .setName("TEST")
                .setValue("4351297464431500")
                .build(),
                ReferenceType.newBuilder()
                .setTyp("41")
                .setName("TEST")
                .setValue("4351297464431501")
                .build());
    }
}
