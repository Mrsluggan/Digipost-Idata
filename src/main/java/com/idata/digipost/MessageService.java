package com.idata.digipost;

import java.io.*;

import java.util.*;
import java.util.logging.Logger;

import com.idata.digipost.config.SignerConfig;
import lombok.extern.slf4j.Slf4j;
import no.digipost.api.client.representations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import no.digipost.api.client.DigipostClient;

@Slf4j
@Service
public class MessageService {

    private final SignerConfig signerConfig;
    private final DigipostClient client;
    private final Logger logger = Logger.getLogger(MessageService.class.getName()); // Initialize logger

    @Autowired
    public MessageService(SignerConfig signerConfig) {
        this.signerConfig = signerConfig;
        this.client = signerConfig.getClient();
    }

    public String sendMessage(String recipient, String subject, List<MultipartFile> document) {
        handleEmptyInput(document, recipient,subject);
        // Hittar användare
        logger.info("Sending message to: " + recipient);
        PersonalIdentificationNumber pin = new PersonalIdentificationNumber(recipient);


        // Skapar primär dokumentet
        UUID documentUuid = UUID.randomUUID();
        Document primaryDocument = new Document(documentUuid, subject, FileType.fromFilename(document.get(0).getOriginalFilename()));


        // Kollar om det finns några extra dokument i anropet
        List<Document> attachments = handleAttachments(document);

        Message message = Message.newMessage("messageId", primaryDocument).recipient(pin).attachments(attachments).build();

        try {
            var messageBuilder = client.createMessage(message).addContent(primaryDocument, document.get(0).getBytes());
            if (!document.isEmpty()) {
                for (int i = 0; i < attachments.size(); i++) {
                    messageBuilder = messageBuilder.addContent(attachments.get(i), document.get(i + 1).getBytes());
                }
            }

            messageBuilder.send();
            logger.info("message sent");


            // TODO hitta bättre return type
            return "message sent";
        } catch (IOException e) {
            logger.warning("Error while sending message: " + e);
            e.printStackTrace();
            return null;
        }
    }


    // TODO ändra när objekt är skapat
    private void  handleEmptyInput(List<MultipartFile> document, String recipient,String subject) {
        if (recipient == null || subject == null || document == null || document.isEmpty()) {
            String message = recipient == null ? "Recipient is null" :
                    subject == null ? "No subject found" :
                            "No documents found";
            logger.warning(message);
            throw new IllegalArgumentException(message);
        }
    }

    private List<Document> handleAttachments(List<MultipartFile> document){

        List<Document> attachments = new ArrayList<>();
        if (!document.isEmpty()) {
            for (int i = 1; i < document.size(); i++) {
                attachments.add(new Document(UUID.randomUUID(), document.get(i).getOriginalFilename(), FileType.fromFilename(document.get(i).getOriginalFilename())));
            }
        }
        return attachments;
    }





}

