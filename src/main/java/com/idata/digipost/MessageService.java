package com.idata.digipost;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

import com.idata.digipost.config.SignerConfig;
import no.digipost.api.client.representations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import no.digipost.api.client.DigipostClient;
import no.digipost.api.client.DigipostClientConfig;
import no.digipost.api.client.SenderId;
import no.digipost.api.client.security.Signer;

import static no.digipost.api.client.security.Signer.usingKeyFromPKCS12KeyStore;

@Service
public class MessageService {

    private final SignerConfig signerConfig;
    private final DigipostClient client;

    @Autowired
    public MessageService(SignerConfig signerConfig) {
        this.signerConfig = signerConfig;
        this.client = signerConfig.getClient();
    }

    public MessageDelivery sendMessage(String subject, List<MultipartFile> document) {
        // Hittar användare
        PersonalIdentificationNumber pin = new PersonalIdentificationNumber("19906997420");

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

    private static InputStream getCertificate() {
        try {
            return new FileInputStream(new File("src/main/resources/certificate-152138.p12"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot read certificate file: " + e.getMessage(), e);
        }
    }
}

