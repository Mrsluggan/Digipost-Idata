package com.idata.digipost.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrintDetailsDTO {

    private String name;
    private String recipientsAddress;
    private String city;
    private String state;
    private String zip;
    private String country;

}
