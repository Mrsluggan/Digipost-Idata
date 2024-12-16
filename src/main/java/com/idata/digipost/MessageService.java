package com.idata.digipost;

import java.io.*;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Logger;

import com.idata.digipost.Models.InvoiceDTO;
import com.idata.digipost.config.SignerConfig;
import lombok.extern.slf4j.Slf4j;
import no.digipost.api.client.representations.*;
import no.digipost.api.datatypes.types.invoice.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import no.digipost.api.client.DigipostClient;

import javax.print.Doc;

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

    public String sendMessage(List<MultipartFile> documents, Request request) {
        handleEmptyInput(documents, request);

        // Hittar användare
        logger.info("Sending message to: " + request.getRecipient());
        PersonalIdentificationNumber pin = new PersonalIdentificationNumber(request.getRecipient());


        // Skapar primär dokumentet
        Document primaryDocument = handlePrimaryDocument(request, documents.get(0).getOriginalFilename());


        // Kollar om det finns några extra dokument i anropet
        List<Document> attachments = handleAttachments(documents);

        Message message = Message.newMessage("messageId", primaryDocument).recipient(pin).attachments(attachments).build();

        try {
            var messageBuilder = client.createMessage(message).addContent(primaryDocument, documents.get(0).getBytes());
            if (!documents.isEmpty()) {
                for (int i = 0; i < attachments.size(); i++) {
                    messageBuilder = messageBuilder.addContent(attachments.get(i), documents.get(i + 1).getBytes());
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


    private Document handlePrimaryDocument(Request request, String document) {

        return switch (request.getType()) {
            case "invoice" -> invoiceBuilder(request, document);
            case "letter" -> letterBuilder(document);
            default -> null;
        };


    }


    public Document invoiceBuilder(Request request, String document) {
        InvoiceDTO invoice = request.getInvoice();
        return new Document(UUID.randomUUID(), request.getSubject(), FileType.fromFilename(document), new Invoice(invoice.getLink(), invoice.getDueDate(), invoice.getSum(), invoice.getCreditorAccount(), invoice.getKid()));
    }

    private Document letterBuilder(String document) {

        return new Document(UUID.randomUUID(), document, FileType.fromFilename(document));
    }


    private List<Document> handleAttachments(List<MultipartFile> document) {

        List<Document> attachments = new ArrayList<>();
        if (!document.isEmpty()) {
            for (int i = 1; i < document.size(); i++) {
                attachments.add(new Document(UUID.randomUUID(), document.get(i).getOriginalFilename(), FileType.fromFilename(document.get(i).getOriginalFilename())));
            }
        }
        return attachments;
    }

    // TODO ändra när objekt är skapat
    private void handleEmptyInput(List<MultipartFile> document, Request request) {
        if (request == null || document == null || document.isEmpty()) {
            String message = request == null ? "request is null" :
                    "No documents found";
            logger.warning(message);
            throw new IllegalArgumentException(message);
        }
    }

}

