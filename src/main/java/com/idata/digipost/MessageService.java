package com.idata.digipost;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import no.digipost.api.client.DigipostClient;
import no.digipost.api.client.DigipostClientConfig;
import no.digipost.api.client.SenderId;
import no.digipost.api.client.representations.Document;
import no.digipost.api.client.representations.FileType;
import no.digipost.api.client.representations.Message;
import no.digipost.api.client.representations.PersonalIdentificationNumber;
import no.digipost.api.client.security.Signer;

@Service
public class MessageService {
    Signer signer;
    SenderId senderId = SenderId.of(152138);

    URI apiUri = URI.create("https://api.test.digipost.no");
    DigipostClientConfig config = DigipostClientConfig.newConfiguration().digipostApiUri(apiUri).build();

    DigipostClient client = new DigipostClient(
            config, senderId.asBrokerId(), signer);

    public Message sendMessage(MultipartFile document) {

        PersonalIdentificationNumber pin = new PersonalIdentificationNumber("26079833787");
        UUID documentUuid = UUID.randomUUID();
        Document primaryDocument = new Document(documentUuid, "Document subject", FileType.PDF);

        Message message = Message.newMessage("messageId", primaryDocument)
                .recipient(pin)
                .build();

        try {
            client.createMessage(message)
                    .addContent(primaryDocument, document.getBytes())
                    .send();
        } catch (IOException e) {
            
            e.printStackTrace();
        }

        return message;
    }

}
