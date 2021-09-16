package net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders;

import MSK.com.gems.EquipmentType;
import MSK.com.gems.MoveType;
import MSK.com.gems.SealType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class EquipmentTestDataBuilder {

    private EquipmentTestDataBuilder(){}

    public static List<EquipmentType> getEquipmentList() {
        return List.of(getEquipment1(), getEquipment2());
    }

    public static List<EquipmentType> getEquipmentList1() {
        return List.of(getEquipment3());
    }

    private static EquipmentType getEquipment2() {
        return getEquipmentType("TCNU6816702");
    }

    private static EquipmentType getEquipment3() {
        return getEquipmentTypeWithMoveTypeKolkata("TCNU6816701");
    }

    private static EquipmentType getEquipmentType(String eqptNum) {
        EquipmentType equipmentType = getCommonEquipment(eqptNum);
        equipmentType.setMove(getMoveTypeWithCopenhagen());

        return equipmentType;
    }
    private static EquipmentType getEquipmentTypeWithMoveTypeKolkata(String eqptNum) {
        EquipmentType equipmentType = getCommonEquipment(eqptNum);
        equipmentType.setMove(getMoveTypeWithKolkataLocation());

        return equipmentType;
    }

    public static EquipmentType getCommonEquipment(String eqptNum) {
        EquipmentType equipmentType = new EquipmentType();
        equipmentType.setEqptNo(eqptNum);
        return equipmentType;
    }

    public static List<EquipmentType> getEquipmentTypeWithSeal() {
        var equipmentType = getCommonEquipment("TCNU6816702");
        equipmentType.setMove(getMoveTypeWithSeal());

        return List.of(equipmentType);
    }

    private static MoveType getMoveTypeWithCopenhagen() {

        MoveType moveType = commonMoveType();
        //TO match the Transportplan type in Transport event test data End Location
        moveType.setActLoc("Copenhagen");
        return moveType;

    }

    @NotNull
    private static MoveType commonMoveType() {
        MoveType moveType = new MoveType();
        moveType.setOperator("MSK");
        moveType.setActDte("2021-09-23");
        moveType.setActTim("23:45:11");
        moveType.setLineCde("LineCode");
        return moveType;
    }

    private static MoveType getMoveTypeWithKolkataLocation() {

        MoveType moveType = commonMoveType();
        //TO match the Transportplan type in Transport event test data End Location
        moveType.setActLoc("Kolkata");
        return moveType;

    }

    private static MoveType getMoveTypeWithSeal() {
        var plainMoveType = getMoveTypeWithKolkataLocation();
        plainMoveType.setSeal(getSeals());
        return plainMoveType;
    }

    private static List<SealType> getSeals() {
        return List.of(new SealType("MAERSK", "MaerskSeal"),
                new SealType("SHIPPER", "ShipperValue"),
                new SealType("VET", "VetValue"),
                new SealType("CUSTOMS", "CustomsValue"));
    }

    private static EquipmentType getEquipment1() {
        return getEquipmentType("TCNU6816701");
    }
}
