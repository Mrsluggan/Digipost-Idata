package com.idata.digipost.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.digipost.api.datatypes.types.ExternalLink;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceDTO {

    public InvoiceDTO(String string, String string2, double d, String creditorAccount2, String kid2) {
        // TODO Auto-generated constructor stub
    }

    private ExternalLink link;
    private ZonedDateTime dueDate;
    private BigDecimal sum;
    private String creditorAccount;
    private String kid;

}
