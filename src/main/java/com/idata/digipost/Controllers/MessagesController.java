package com.idata.digipost.Controllers;

import com.idata.digipost.Models.Request;
import com.idata.digipost.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.logging.Logger;


@Slf4j
@RestController
@RequestMapping("/api/message")
public class MessagesController {

    MessageService messageService;
    private final Logger logger = Logger.getLogger(MessagesController.class.getName()); // Initialize logger


    public MessagesController(MessageService messageService) {
        this.messageService = messageService;
    }


    // TODO gör klass eller objekt för filerna som skickas, kanske något Messagedto
    @PostMapping()
    public ResponseEntity<Request> sendMessage(@RequestPart List<MultipartFile> document, @RequestPart Request request) {
        logger.info("Request: " + request.toString());
        // Dessa behövs inte, bara här för testing
        logger.info("Number of documents: " + document.size());
        for (MultipartFile file : document) {
            logger.info(file.getOriginalFilename());
        }
        try {
            logger.info("Starting to send message");
            return ResponseEntity.ok(messageService.sendMessage(document, request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}
