package net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders;

import com.maersk.jaxb.pojo.EquipmentType;
import com.maersk.jaxb.pojo.MoveType;

import java.util.List;

public final class EquipmentTestDataBuilder {

    private EquipmentTestDataBuilder(){}

    public static List<EquipmentType> getEquipmentList() {
        return List.of(getEquipment1(), getEquipment2());
    }

    private static EquipmentType getEquipment2() {
        return getEquipmentType("TCNU6816702");
    }

    private static EquipmentType getEquipmentType(String eqptNum) {
        EquipmentType equipmentType = new EquipmentType();
        equipmentType.setEqptNo(eqptNum);
        equipmentType.setMove(getMoveType());

        return equipmentType;
    }

    private static MoveType getMoveType() {

        MoveType moveType = new MoveType();
        moveType.setOperator("MSK");
        moveType.setActDte("2021-09-23 ");
        moveType.setActTim("23:45");
        return moveType;

    }

    private static EquipmentType getEquipment1() {
        return getEquipmentType("TCNU6816701");
    }
}
