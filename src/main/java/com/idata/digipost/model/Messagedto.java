package com.idata.digipost.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Messagedto {

    private String subject;
    private List<MultipartFile> document;


}
