package com.idata.digipost;

import no.digipost.api.client.*;
import no.digipost.api.client.DigipostClient;
import no.digipost.api.client.DigipostClientConfig;
import no.digipost.api.client.SenderId;
import no.digipost.api.client.representations.Document;
import no.digipost.api.client.representations.FileType;
import no.digipost.api.client.representations.Message;
import no.digipost.api.client.representations.PersonalIdentificationNumber;
import no.digipost.api.client.security.Signer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
public class MessagesController {

    private String senderid;

    private String secret;

    SenderId senderId = SenderId.of(152138);
    Signer signer;
    try


    URI apiUri = URI.create("https://api.test.digipost.no");
    DigipostClientConfig config = DigipostClientConfig.newConfiguration().digipostApiUri(apiUri).build();

    DigipostClient client = new DigipostClient(
            config, senderId.asBrokerId(), signer);

    public MessagesController() throws IOException {
    }


    @PostMapping()
    public String sendMessage(@RequestPart MultipartFile pdfFile) throws IOException {
        PersonalIdentificationNumber pin = new PersonalIdentificationNumber("26079833787");
        UUID documentUuid = UUID.randomUUID();
        Document primaryDocument = new Document(documentUuid, "Document subject", FileType.PDF);

        Message message = Message.newMessage("messageId", primaryDocument)
                .recipient(pin)
                .build();

        client.createMessage(message)
                .addContent(primaryDocument, pdfFile.getBytes())
                .send();
        return "hello";
    }


    @GetMapping()
    public String getMessage(){
        System.out.println(secret);
        System.out.println(senderid);
        return "hello";
    }


}
