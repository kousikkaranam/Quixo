package org.quixo.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.quixo.model.SharedItem;
import org.quixo.mongo.SharedFile;
import org.quixo.mongo.SharedFileRepository;
import org.quixo.mongo.SharedText;
import org.quixo.mongo.SharedTextRepository;
import org.quixo.service.ShareService;
import org.quixo.repository.SharedItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShareController {

    @Autowired
    private SharedItemRepository repository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private ShareService shareService;

    @Autowired
    private SharedTextRepository textRepository;

    @Autowired
    private SharedFileRepository fileRepository;

    @PostMapping("/share")
    public ResponseEntity<Map<String, String>> share(@ModelAttribute org.quixo.dto.ShareRequest request) throws IOException {
        String code = shareService.saveSharedItem(request);
        return ResponseEntity.ok(Collections.singletonMap("link", "http://localhost:8080/view/" + code));
    }

    @GetMapping("/retrieve/{code}")
    public ResponseEntity<?> retrieve(@PathVariable String code) throws IOException {
        SharedItem item = repository.findByCode(code);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        // Prepare a response map
        Map<String, Object> responseBody = new HashMap<>();

        if ("TEXT".equals(item.getType())) {
            SharedText sharedText = textRepository.findById(item.getContentId()).orElse(null);
            if (sharedText == null) {
                responseBody.put("error", "Text content not found in MongoDB");
            } else {
                String textContent = sharedText.getContent();
                if (textContent.trim().isEmpty()) {
                    textContent = "api has retrieved/code only";
                }
                responseBody.put("text", textContent);
            }
        } else if ("FILE".equals(item.getType())) {
            SharedFile sharedFile = fileRepository.findById(item.getContentId()).orElse(null);
            if (sharedFile == null) {
                responseBody.put("error", "File content not found in MongoDB");
            } else {
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("fileName", sharedFile.getFileName());
                fileInfo.put("contentType", sharedFile.getContentType());
                fileInfo.put("data", sharedFile.getData()); // Consider streaming for large files
                responseBody.put("file", fileInfo);
            }
        } else if ("BOTH".equals(item.getType())) {
            // Expect contentId to be in the form "textId:fileId"
            String[] parts = item.getContentId().split(":");
            if (parts.length == 2) {
                SharedText sharedText = textRepository.findById(parts[0]).orElse(null);
                SharedFile sharedFile = fileRepository.findById(parts[1]).orElse(null);

                if (sharedText != null) {
                    String textContent = sharedText.getContent();
                    if (textContent.trim().isEmpty()) {
                        textContent = "api has retrieved/code only";
                    }
                    responseBody.put("text", textContent);
                }
                if (sharedFile != null) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("fileName", sharedFile.getFileName());
                    fileInfo.put("contentType", sharedFile.getContentType());
                    fileInfo.put("data", sharedFile.getData());
                    responseBody.put("file", fileInfo);
                }
            } else {
                responseBody.put("error", "Invalid contentId format for type both");
            }
        }

        if (responseBody.isEmpty()) {
            responseBody.put("error", "No content found for code " + code);
        }
        return ResponseEntity.ok(responseBody);
    }

}
