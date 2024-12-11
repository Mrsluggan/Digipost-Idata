package com.idata.digipost;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.logging.Logger;

import com.idata.digipost.config.SignerConfig;
import lombok.extern.slf4j.Slf4j;
import no.digipost.api.client.representations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import no.digipost.api.client.DigipostClient;
import no.digipost.api.client.DigipostClientConfig;
import no.digipost.api.client.SenderId;
import no.digipost.api.client.security.Signer;

import static no.digipost.api.client.security.Signer.usingKeyFromPKCS12KeyStore;

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
        if (document == null || document.isEmpty() || recipient == null) {
            logger.info("No documents or recipient found");
            return null;
        }
        // Hittar användare
        PersonalIdentificationNumber pin = new PersonalIdentificationNumber(recipient);
        logger.info("Sending message to: " + recipient);
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

            messageBuilder.send();
            logger.info("message sent");

            return "message sent";
        } catch (IOException e) {
            logger.warning("Error while sending message: " + e);
            e.printStackTrace();
            return null;
        }


    }

}

