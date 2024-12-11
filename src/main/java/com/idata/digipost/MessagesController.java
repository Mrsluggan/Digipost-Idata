package com.idata.digipost;

import no.digipost.api.client.representations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;


@RestController
@RequestMapping("/api/message")
public class MessagesController {

   MessageService messageService;
    private final Logger logger = Logger.getLogger(MessagesController.class.getName()); // Initialize logger


   public MessagesController(MessageService messageService){
       this.messageService = messageService;
    }

    @PostMapping()
    public ResponseEntity<String> sendMessage(@RequestPart List<MultipartFile> document, String subject,String recipient){

       // Dessa behövs inte, bara här för testing
        for (MultipartFile file : document) {
            logger.info("These are the files: "+file.getOriginalFilename());
        }
        try {
            logger.info("Starting to send message");
            messageService.sendMessage(recipient,subject,document);
            logger.info("Message sent");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


   }


    @GetMapping()
    public String getMessage(){

        return "hello";
    }


}
