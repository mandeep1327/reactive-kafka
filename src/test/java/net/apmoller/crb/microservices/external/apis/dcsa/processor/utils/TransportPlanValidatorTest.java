package net.apmoller.crb.microservices.external.apis.dcsa.processor.utils;

import MSK.com.gems.EndLocType;
import MSK.com.gems.StartLocType;
import org.junit.jupiter.api.Test;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getValidTransportPlan;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TransportPlanValidatorTest {

    @Test
    public void testValidTransportPlan() {
        assertTrue(TransportPlanValidator.validate(getValidTransportPlan()));
    }

    @Test
    public void testNoStartLocTransportPlan() {
        var validTransportPlan = getValidTransportPlan();
        validTransportPlan.setStartLoc(null);
        assertFalse(TransportPlanValidator.validate(validTransportPlan));
        validTransportPlan.setStartLoc(new StartLocType());
        assertFalse(TransportPlanValidator.validate(validTransportPlan));
    }

    @Test
    public void testNoEndLocTransportPlan() {
        var validTransportPlan = getValidTransportPlan();
        validTransportPlan.setEndLoc(null);
        assertFalse(TransportPlanValidator.validate(validTransportPlan));
        validTransportPlan.setEndLoc(new EndLocType());
        assertFalse(TransportPlanValidator.validate(validTransportPlan));
    }

    @Test
    public void testNoLegSeqTransportPlan() {
        var validTransportPlan = getValidTransportPlan();
        validTransportPlan.setLegSeq(null);
        assertFalse(TransportPlanValidator.validate(validTransportPlan));
    }

    @Test
    public void testNoDepTSTransportPlan() {
        var validTransportPlan = getValidTransportPlan();
        validTransportPlan.setGcssexpDepTS(null);
        assertFalse(TransportPlanValidator.validate(validTransportPlan));
    }

    @Test
    public void testNoArvTSTransportPlan() {
        var validTransportPlan = getValidTransportPlan();
        validTransportPlan.setGttsactArvTS(null);
        assertFalse(TransportPlanValidator.validate(validTransportPlan));
    }

    @Test
    public void testOneDepTSIsEnoughTransportPlan() {
        var validTransportPlan = getValidTransportPlan();
        validTransportPlan.setGcssexpDepTS(null);
        validTransportPlan.setGttsexpDepTS("2021-07-21 13:29");
        assertTrue(TransportPlanValidator.validate(validTransportPlan));
        validTransportPlan.setGttsexpDepTS(null);
        validTransportPlan.setGsisexpDepTS("2021-07-21 13:29");
        assertTrue(TransportPlanValidator.validate(validTransportPlan));
        validTransportPlan.setGsisexpDepTS(null);
        validTransportPlan.setGttsactDepTS("2021-07-21 13:29");
        assertTrue(TransportPlanValidator.validate(validTransportPlan));
    }

    @Test
    public void testOneArvTSIsEnoughTransportPlan() {
        var validTransportPlan = getValidTransportPlan();
        validTransportPlan.setGttsactArvTS(null);
        validTransportPlan.setGcssexpArvTS("2021-07-21 13:29");
        assertTrue(TransportPlanValidator.validate(validTransportPlan));
        validTransportPlan.setGcssexpArvTS(null);
        validTransportPlan.setGttsexpArvTS("2021-07-21 13:29");
        assertTrue(TransportPlanValidator.validate(validTransportPlan));
        validTransportPlan.setGttsexpArvTS(null);
        validTransportPlan.setGsisexpArvTS("2021-07-21 13:29");
        assertTrue(TransportPlanValidator.validate(validTransportPlan));
    }

    @Test
    public void testNullValuesTransportPlan() {
        assertFalse(TransportPlanValidator.validate(null));
    }
}
