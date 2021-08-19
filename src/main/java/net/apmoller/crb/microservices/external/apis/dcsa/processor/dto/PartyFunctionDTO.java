package net.apmoller.crb.microservices.external.apis.dcsa.processor.dto;

import MSK.com.external.dcsa.PartyFunctionCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartyFunctionDTO {
    private PartyFunctionCode functionCode;
    private String functionName;
}
