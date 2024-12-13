package com.idata.digipost.service;

import java.io.*;
import java.util.*;

import com.idata.digipost.config.SignerConfig;
import no.digipost.api.client.representations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import no.digipost.api.client.DigipostClient;
import org.slf4j.Logger;

@Service
public class MessageService {

    private final SignerConfig signerConfig; 
    private final DigipostClient client; 
    private final Logger logger;

    @Autowired
    public MessageService(SignerConfig signerConfig, Logger logger) {
        this.signerConfig = signerConfig;
        this.client = signerConfig.getClient(); 
        this.logger = logger;
    }

    public MessageDelivery sendMessage(String recipient, String subject, List<MultipartFile> document) {
        // Hittar användare
        PersonalIdentificationNumber pin = new PersonalIdentificationNumber(recipient);

        // Skapar primär dokumentet
        UUID documentUuid = UUID.randomUUID();
        Document primaryDocument = new Document(documentUuid, subject, FileType.fromFilename(document.get(0).getOriginalFilename()));


        // Kollar om det finns några extra dokument i anropet
        List<Document> attachments = new ArrayList<>();
        if (!document.isEmpty()) {
            for (int i = 1; i < document.size(); i++) {
                attachments.add(new Document(UUID.randomUUID(), document.get(i).getOriginalFilename(), FileType.fromFilename(document.get(i).getOriginalFilename())));
            }
        }

        Message message = Message.newMessage("messageId", primaryDocument).recipient(pin).attachments(attachments).build();

        try {
            var messageBuilder = client.createMessage(message).addContent(primaryDocument, document.get(0).getBytes());
            if (!document.isEmpty()) {
                for (int i = 0; i < attachments.size(); i++) {
                    messageBuilder = messageBuilder.addContent(attachments.get(i), document.get(i + 1).getBytes());
                }
            }
            return messageBuilder.send();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

}

