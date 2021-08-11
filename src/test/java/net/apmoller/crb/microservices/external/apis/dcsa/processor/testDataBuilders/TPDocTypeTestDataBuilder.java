package net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders;

import com.maersk.jaxb.pojo.TPDocType;

import java.util.List;

public final class TPDocTypeTestDataBuilder {
    private TPDocTypeTestDataBuilder(){}

    public static List<TPDocType> getTPDocList() {
        return List.of(getTPDocType1(), getTPDocType2());
    }

    private static TPDocType getTPDocType1() {

        var tpDocType = new TPDocType();
        tpDocType.setBolNo("293156737");
        tpDocType.setOperator("Operator");
        return tpDocType;

    }

    private static TPDocType getTPDocType2() {

        var tpDocType = new TPDocType();
        tpDocType.setBolNo("240002126");
        return tpDocType;

    }

}
