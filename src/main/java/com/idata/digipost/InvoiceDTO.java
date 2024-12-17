package com.idata.digipost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    private String link;
    private String dueDate;
    private Double sum;
    private String creditorAccount;
    private String kid;
}
