package com.idata.digipost;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import no.digipost.api.client.representations.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import no.digipost.api.client.DigipostClient;
import no.digipost.api.client.DigipostClientConfig;
import no.digipost.api.client.SenderId;
import no.digipost.api.client.security.Signer;

import static no.digipost.api.client.security.Signer.usingKeyFromPKCS12KeyStore;

@Service
public class MessageService {
    SenderId senderId = SenderId.of(152138);

    Signer signer = usingKeyFromPKCS12KeyStore(getCertificate(),"IDATA2024!");




    URI apiUri = URI.create("https://api.test.digipost.no");
    DigipostClientConfig config = DigipostClientConfig.newConfiguration().digipostApiUri(apiUri).build();
    DigipostClient client = new DigipostClient(config, senderId.asBrokerId(), signer);


    public MessageService()  {
        System.out.println(signer);

    }


    public MessageDelivery sendMessage(MultipartFile document) {


        PersonalIdentificationNumber pin = new PersonalIdentificationNumber("19906997420");
        UUID documentUuid = UUID.randomUUID();
        Document primaryDocument = new Document(documentUuid, "Documet subject", FileType.PDF);

        Message message = Message.newMessage("messageId", primaryDocument)
                .recipient(pin)
                .build();

        try {
            return client.createMessage(message)
                    .addContent(primaryDocument, document.getBytes())
                    .send();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static InputStream getCertificate() {
        try {
            return new FileInputStream(new File("src/main/resources/certificate-152138.p12"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Kunne ikke lese sertifikatfil: " + e.getMessage(), e);
        }
    }


}
