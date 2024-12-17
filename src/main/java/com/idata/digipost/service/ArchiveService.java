package com.idata.digipost.service;
// package com.idata.digipost;

// import java.io.InputStream;
// import java.net.URI;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;

// import org.springframework.stereotype.Service;

// import com.idata.digipost.ArchiveDocument;

// import com.idata.digipost.Archive;

// @Service

// public class ArchiveService {

// public ArchiveRequest createArchiveRequest(Archive archive) {
// return new ArchiveRequest(archive);
// }

// public ArchivesResponse getArchives(long senderId) {
// return new ArchivesResponse();
// }

// public ArchiveDocument getArchiveDocumentByUuid(UUID uuid) {
// return new ArchiveDocument(uuid, "example.pdf", "file", "application/pdf");
// }

// public InputStream getArchiveDocumentContentStream(URI contentStreamUri) {
// return null;
// }

// public static class ArchiveRequest {
// private final Archive archive;
// private final List<ArchiveDocument> documents = new ArrayList<>();

// public ArchiveRequest(Archive archive) {
// this.archive = archive;
// }

// public void addFile(ArchiveDocument document, InputStream content) {
// documents.add(document);
// }

// public void send() {
// }
// }

// public static class ArchivesResponse {
// private final List<ArchiveDocument> documents = new ArrayList<>();

// public List<ArchiveDocument> getDocuments() {
// return documents;
// }
// }
// }
