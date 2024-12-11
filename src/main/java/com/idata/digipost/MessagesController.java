package com.idata.digipost;

import no.digipost.api.client.representations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/message")
public class MessagesController {

   MessageService messageService;

   public MessagesController(MessageService messageService){
       this.messageService = messageService;
    }

    @PostMapping()
    public ResponseEntity<MessageDelivery> sendMessage(@RequestPart List<MultipartFile> document, String subject,String recipient) throws IOException {
        for (MultipartFile file : document) {
            System.out.println(file.getOriginalFilename());
        }


       System.out.println(subject);
        return ResponseEntity.ok(messageService.sendMessage(recipient,subject,document));
    }


    @GetMapping()
    public String getMessage(){

        return "hello";
    }


}
