// package com.idata.digipost.archive;

// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.UUID;

// @RestController
// @RequestMapping("/api/archive") // Correct mapping
// public class ArchiveController {

//     private final ArchiveService archiveService;

// public ArchiveController(ArchiveService archiveService) {
//     this.archiveService = archiveService;
// }


//     @PostMapping
//     public void createArchive(@RequestBody ArchiveRequestBody requestBody) {
//         Archive archive = Archive.builder()
//                 .documents(requestBody.getDocuments())
//                 .senderId(requestBody.getSenderId())
//                 .build();

//         ArchiveService.ArchiveRequest request = archiveService.createArchiveRequest(archive);
//         requestBody.getDocuments().forEach(doc -> request.addFile(doc, null));
//         request.send();
//     }


//     @GetMapping("/{senderId}")
//     public List<ArchiveDocument> getArchives(@PathVariable long senderId) {
//         return archiveService.getArchives(senderId).getDocuments();
//     }

//     @GetMapping("/document/{uuid}")
//     public Archive getArchiveDocumentByUuid(@PathVariable UUID uuid) {
//         return archiveService.getArchiveDocumentByUuid(uuid);
//     }

//     public static class ArchiveRequestBody {
//         private List<ArchiveDocument> documents;
//         private long senderId;

//         public List<ArchiveDocument> getDocuments() {
//             return documents;
//         }

//         public void setDocuments(List<ArchiveDocument> documents) {
//             this.documents = documents;
//         }

//         public long getSenderId() {
//             return senderId;
//         }

//         public void setSenderId(long senderId) {
//             this.senderId = senderId;
//         }
//     }

  

// }



