package com.idata.digipost.service;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import com.idata.digipost.config.SignerConfig;

import com.idata.digipost.model.Request;
import com.idata.digipost.model.SendMessageResponse;
import com.idata.digipost.model.PrintDetailsDTO;

import lombok.extern.slf4j.Slf4j;
import no.digipost.api.client.representations.*;
import no.digipost.api.datatypes.types.invoice.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import no.digipost.api.client.DigipostClient;
import no.digipost.api.client.SenderId;

@Slf4j
@Service
public class MessageService {

    private final DigipostClient client;
    private static final Logger LOGGER = Logger.getLogger(MessageService.class.getName());

    @Autowired
    public MessageService(SignerConfig signerConfig) {
        this.client = signerConfig.getClient();
    }

    public SendMessageResponse sendMessage(List<MultipartFile> documents, Request request) {
        validateInput(documents, request);

        LOGGER.info("Sending message to: " + request.getRecipient());
        PersonalIdentificationNumber pin = new PersonalIdentificationNumber(request.getRecipient());

        Document primaryDocument = createPrimaryDocument(request, documents.get(0).getOriginalFilename());
        List<Document> attachments = createAttachments(documents);

        Message message = Message.newMessage("messageId", primaryDocument)
                .recipient(pin)
                .attachments(attachments)
                .build();

        try {
            var messageBuilder = client.createMessage(message)
                    .addContent(primaryDocument, documents.get(0).getBytes());

            for (int i = 0; i < attachments.size(); i++) {
                messageBuilder = messageBuilder.addContent(attachments.get(i), documents.get(i + 1).getBytes());
            }

            messageBuilder.send();
            LOGGER.info("Message sent successfully");
            return new SendMessageResponse(request, primaryDocument.uuid);
        } catch (IOException e) {
            LOGGER.severe("Error while sending message: " + e.getMessage());
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public DocumentStatus getDocumentStatus(String senderId, UUID documentUuid) { 
        try { 
            DocumentStatus status = client.getDocumentStatus(SenderId.of(Long.parseLong(senderId)), documentUuid); 
            LOGGER.info("Document status: " + status.status); 
            LOGGER.info("Delivery channel: " + status.channel); 
            return status; 
        } catch (Exception e) { 
            LOGGER.severe("Error while fetching document status: " + e.getMessage()); return null; 
        }
    }

    private Document createPrimaryDocument(Request request, String document) {
        return switch (request.getType()) {
            case "invoice" -> createInvoiceDocument(request, document);
            case "letter" -> createLetterDocument(document);
            case "letterWithSmsNotification" -> createLetterWithSmsNotificationDocument(request, document);
            default -> throw new IllegalArgumentException("Unsupported document type: " + request.getType());
        };
    }

    private Document createInvoiceDocument(Request request, String document) {
        LOGGER.info("Creating invoice");
        com.idata.digipost.model.InvoiceDTO invoice = request.getInvoice();
        return new Document(
                UUID.randomUUID(),
                request.getSubject(),
                FileType.fromFilename(document),
                new Invoice(
                        invoice.getLink(),
                        invoice.getDueDate(),
                        invoice.getSum(),
                        invoice.getCreditorAccount(),
                        invoice.getKid()));
    }

    private Document createLetterDocument(String document) {
        LOGGER.info("Creating letter");
        return new Document(UUID.randomUUID(), document, FileType.fromFilename(document));
    }

    private Message createMessage(PersonalIdentificationNumber pin, Request request, Document primaryDocument,
            List<Document> attachments) {
        if (request.getPrintDetails() != null) {
            return Message.newMessage("messageId", primaryDocument)
                    .recipient(new MessageRecipient(pin, createPhysicalLetterDocument(request)))
                    .attachments(attachments)
                    .build();
        } else {
            return Message.newMessage("messageId", primaryDocument)
                    .recipient(pin)
                    .attachments(attachments)
                    .build();
        }

    }

    private PrintDetails createPhysicalLetterDocument(Request request) {

        PrintDetailsDTO printDetailsDTO = request.getPrintDetails();

        return new PrintDetails(
                new PrintRecipient(printDetailsDTO.getRecipientAddress().getName(),
                        new NorwegianAddress(printDetailsDTO.getRecipientAddress().getAddress(),
                                printDetailsDTO.getRecipientAddress().getZip(),
                                printDetailsDTO.getRecipientAddress().getCity())),
                new PrintRecipient(printDetailsDTO.getReturnAddress().getName(),
                        new NorwegianAddress(printDetailsDTO.getReturnAddress().getAddress(),
                                printDetailsDTO.getReturnAddress().getZip(),
                                printDetailsDTO.getReturnAddress().getCity())),
                PrintDetails.PrintColors.MONOCHROME, PrintDetails.NondeliverableHandling.RETURN_TO_SENDER);
    }

    private Document createLetterWithSmsNotificationDocument(Request request, String document) {
        LOGGER.info("Creating letter with SMS notification");
        return new Document(
                UUID.randomUUID(),
                document,
                FileType.fromFilename(document),
                null,
                new SmsNotification(1),
                null,
                AuthenticationLevel.PASSWORD,
                SensitivityLevel.NORMAL);
    }

    private List<Document> createAttachments(List<MultipartFile> documents) {
        LOGGER.info("Creating attachments");

        if (documents.size() <= 1) {
            return Collections.emptyList();
        }

        List<Document> attachments = new ArrayList<>();
        for (int i = 1; i < documents.size(); i++) {
            attachments.add(new Document(
                    UUID.randomUUID(),
                    documents.get(i).getOriginalFilename(),
                    FileType.fromFilename(documents.get(i).getOriginalFilename())));
        }
        return attachments;
    }

    private void validateInput(List<MultipartFile> documents, Request request) {
        if (request == null || documents == null || documents.isEmpty()) {
            String message = (request == null)
                    ? "Request cannot be null"
                    : "No documents found in the input";
            LOGGER.severe(message);
            throw new IllegalArgumentException(message);
        }
    }

    public void sendSecureLetter(String recipient, String subject, InputStream contentStream) {
        LOGGER.info("Sending secure letter to: " + recipient);

        PersonalIdentificationNumber pin = new PersonalIdentificationNumber(recipient);

        Document secureDocument = new Document(
                UUID.randomUUID(),
                subject,
                FileType.PDF,
                null,
                null,
                null,
                AuthenticationLevel.TWO_FACTOR,
                SensitivityLevel.SENSITIVE);

        Message secureMessage = Message.newMessage(UUID.randomUUID().toString(), secureDocument)
                .recipient(pin)
                .build();

        client.createMessage(secureMessage)
                .addContent(secureDocument, contentStream)
                .send();

        LOGGER.info("Secure letter sent successfully");
    }
}