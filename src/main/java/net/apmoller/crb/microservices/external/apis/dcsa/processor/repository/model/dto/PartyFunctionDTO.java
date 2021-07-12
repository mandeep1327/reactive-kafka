package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.dto;

import lombok.Builder;
import lombok.Data;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party;

@Data
@Builder
public class PartyFunctionDTO {
    private Party.PartyFuncCode functionCode;
    private Party.PartyFuncName functionName;
}
