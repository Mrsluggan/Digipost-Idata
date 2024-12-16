package com.idata.digipost;

import no.digipost.api.client.representations.MessageDelivery;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class MessagesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MessageService messageService;

    @Test
    public void testSendMessageWithValidInputs() throws Exception {
        MockMultipartFile document1 = new MockMultipartFile("document", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "File content".getBytes());
        MockMultipartFile document2 = new MockMultipartFile("document", "file2.txt", MediaType.TEXT_PLAIN_VALUE, "Another file content".getBytes());

         List<MockMultipartFile> documents = List.of(document1, document2);


        String subject = "Test Subject";
        String recipient = "19906997420";
        MultipartFile realFile1 = new MockMultipartFile("document", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "File content".getBytes());
        MultipartFile realFile2 = new MockMultipartFile("document", "file2.txt", MediaType.TEXT_PLAIN_VALUE, "Another file content".getBytes());
        List<MultipartFile> realDocuments = List.of(realFile1, realFile2);

        ResponseEntity<String> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(messageService.sendMessage( realDocuments, null))
                .thenReturn(String.valueOf(expectedResponse.getStatusCode()));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/message")
                        .file(document1)
                        .file(document2)
                        .param("subject", subject)
                        .param("recipient", recipient)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    public void testSendMessageWithEmptyDocuments() throws Exception {
        // Arrange
        String subject = "Test Subject";
        String recipient = "test@example.com";

        Mockito.when(messageService.sendMessage( any(List.class),null))
                .thenThrow(IllegalArgumentException.class);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/message")
                        .param("subject", subject)
                        .param("recipient", recipient)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSendMessageWithMissingParameters() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/message")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}