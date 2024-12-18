package com.idata.digipost.config;

import no.digipost.api.client.DigipostClient;
import no.digipost.api.client.DigipostClientConfig;
import no.digipost.api.client.SenderId;
import no.digipost.api.client.security.Signer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import static no.digipost.api.client.security.Signer.usingKeyFromPKCS12KeyStore;
@Configuration
public class SignerConfig {

    @Value("${digipost.idata.senderid}")
    String senderId;

    @Value("${digipost.idata.certificate}")
    String signer;

    @Bean
    public DigipostClient getClient() {
        SenderId senderId = SenderId.of(152138);
        Signer signer = usingKeyFromPKCS12KeyStore(getCertificate(),"IDATA2024!");
        URI apiUri = URI.create("https://api.test.digipost.no");
        DigipostClientConfig config = DigipostClientConfig.newConfiguration().digipostApiUri(apiUri).build();


        return new DigipostClient(config, senderId.asBrokerId(), signer);
    }

    private static InputStream getCertificate() {
        try {
            return new FileInputStream(new File("src/main/resources/certificate-152138.p12"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Kunne ikke lese sertifikatfil: " + e.getMessage(), e);
        }
    }
}
