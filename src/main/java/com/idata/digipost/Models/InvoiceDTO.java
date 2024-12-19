package com.idata.digipost.Models;

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

<<<<<<< Updated upstream
=======


>>>>>>> Stashed changes
    private ExternalLink link;
    private ZonedDateTime dueDate;
    private BigDecimal sum;
    private String creditorAccount;
    private String kid;


}
