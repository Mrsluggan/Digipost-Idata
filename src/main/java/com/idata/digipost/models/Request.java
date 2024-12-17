package com.idata.digipost.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {

    private String type;
    private String recipient;
    private String subject;
    private InvoiceDTO invoice;
    private PrintDetailsDTO printDetails;


}
