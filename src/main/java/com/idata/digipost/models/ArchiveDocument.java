// package com.idata.digipost;

// import java.util.HashMap;
// import java.util.Map;

// import java.util.UUID;

// public class ArchiveDocument {

//     private final UUID uuid;
//     private final String filename;
//     private final String fileType;
//     private final String mimeType;
//     private final Map<String, String> attributes = new HashMap<>();

//     public ArchiveDocument(UUID uuid, String filename, String fileType, String mimeType) {
//         this.uuid = uuid;
//         this.filename = filename;
//         this.fileType = fileType;
//         this.mimeType = mimeType;
//     }

//     public UUID getUuid() {
//         return uuid;
//     }

//     public String getFilename() {
//         return filename;
//     }

//     public String getFileType() {
//         return fileType;
//     }

//     public String getMimeType() {
//         return mimeType;
//     }

//     public ArchiveDocument addAttribute(String key, String value) {
//         attributes.put(key, value);
//         return this;
//     }

//     public Map<String, String> getAttributes() {
//         return attributes;
//     }

// }
