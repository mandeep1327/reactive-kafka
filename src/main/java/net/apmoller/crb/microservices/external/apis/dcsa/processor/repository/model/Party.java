package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Party {

    public enum PartyFuncCode {
        OS, CN, N1, N2, NI, COW, COX, DDR, DDS
    }

    public enum PartyFuncName {
        BOOKED_BY ("Booked By"),
        CONTRACT ("Contractual Customer"),
        SHIPPER ("Shipper"),
        CONSIGNEE ("Consignee"),
        FNP ("First Notify Party"),
        ANP ("Additional Notify Party"),
        AO ("Allocation Owner"),
        OCB ("Outward Customs Broker"),
        ICB ("Inward Customs Broker"),
        CCIA ("Contractual Carrier''s Inward Agent"),
        IP ("Invoice Party"),
        OF ("Outward Forwarder"),
        IF ("Inward Forwarder"),
        TDR ("Transport Document Receiver"),
        CCOA ("Contractual Carrier''s Outward Agent"),
        CP ("Credit Party"),
        PO ("Product Owner"),
        ODO ("Outward Document Owner"),
        IDO ("Inward Document Owner"),
        RTP ("Release to Party"),
        LH ("Lawful (B/L) Holder"),
        DMIP ("Demurrage Invoice Party"),
        DTIP ("Detention Invoice Party"),
        SUPP ("Supplier"),
        HS ("House Shipper"),
        HC ("House Consignee"),
        SWSH ("Switched Shipper"),
        SWCO ("Switched Consignee");

        public String getLabel() {
            return label;
        }

        private String label;


        PartyFuncName(String label) {
            this.label = label;
        }
    }

    private String partyID;
    private String partyName;
    private PartyFuncCode partyFunctionCode;
    private PartyFuncName partyFunctionName;
}
