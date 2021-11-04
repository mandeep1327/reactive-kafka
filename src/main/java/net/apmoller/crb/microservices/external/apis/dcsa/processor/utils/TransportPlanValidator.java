package net.apmoller.crb.microservices.external.apis.dcsa.processor.utils;

import MSK.com.gems.EndLocType;
import MSK.com.gems.StartLocType;
import MSK.com.gems.TransportPlanType;

import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * A Transport plan is considered valid if it has the following values:
 * StartLoc, EndLoc, XXXXxxxDepTS, XXXXxxxArvTS, LegSeq defined
 */
public class TransportPlanValidator {

    public static boolean validate(TransportPlanType transportPlanType) {
        return nonNull(transportPlanType)
                && hasStartLoc(transportPlanType.getStartLoc()) &&
                hasEndLoc(transportPlanType.getEndLoc()) &&
                notBlank(transportPlanType.getLegSeq()) &&
                hasArvTs(transportPlanType) &&
                hasDepTs(transportPlanType);
    }

    private static boolean hasStartLoc(StartLocType startLocType) {
        return !Optional.ofNullable(startLocType).map(StartLocType::getValue)
                .orElse("")
                .isBlank();
    }

    private static boolean hasEndLoc(EndLocType endLocType) {
        return !Optional.ofNullable(endLocType).map(EndLocType::getValue)
                .orElse("")
                .isBlank();
    }

    private static boolean notBlank(String legSeq) {
        return !Optional.ofNullable(legSeq).orElse("").isBlank();
    }

    private static boolean hasArvTs(TransportPlanType transportPlan) {
        return notBlank(transportPlan.getGcssexpArvTS()) ||
                notBlank(transportPlan.getGttsactArvTS()) ||
                notBlank(transportPlan.getGttsexpArvTS()) ||
                notBlank(transportPlan.getGsisexpArvTS());
    }

    private static boolean hasDepTs(TransportPlanType transportPlan) {
        return notBlank(transportPlan.getGcssexpDepTS()) ||
                notBlank(transportPlan.getGttsactDepTS()) ||
                notBlank(transportPlan.getGttsexpDepTS()) ||
                notBlank(transportPlan.getGsisexpDepTS());
    }
}
