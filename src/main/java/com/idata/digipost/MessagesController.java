package com.idata.digipost;

import no.digipost.api.client.*;
import no.digipost.api.client.DigipostClient;
import no.digipost.api.client.DigipostClientConfig;
import no.digipost.api.client.SenderId;
import no.digipost.api.client.representations.*;
import no.digipost.api.client.security.Signer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.xml.bind.DatatypeConverter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
public class MessagesController {

   MessageService messageService = new MessageService();

   public MessagesController(MessageService messageService){
       this.messageService = messageService;
    }

    @PostMapping()
    public MessageDelivery sendMessage(@RequestPart MultipartFile document) throws IOException {

        return messageService.sendMessage(document) ;
    }


    @GetMapping()
    public String getMessage(){

        return "hello";
    }


}
