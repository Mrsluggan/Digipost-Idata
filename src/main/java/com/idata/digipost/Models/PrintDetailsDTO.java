package com.idata.digipost.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrintDetailsDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RecipientAddress {
        private String name;
        private String address;
        private String city;
        private String zip;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReturnAddress {
        private String name;
        private String address;
        private String city;
        private String zip;
    }

    private RecipientAddress recipientAddress;
    private ReturnAddress returnAddress;
}