package org.quixo.controller;

import org.quixo.dto.ShareRequest;
import org.quixo.model.SharedItem;
import org.quixo.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShareController {
    @Autowired
    private ShareService shareService;

    @PostMapping("/share")
    public ResponseEntity<Map<String, String>> share(@ModelAttribute ShareRequest request) throws IOException {
        String code = shareService.saveSharedItem(request);
        return ResponseEntity.ok(Collections.singletonMap("link", "https://quixo.com/share/" + code));
    }

    @GetMapping("/retrieve/{code}")
    public ResponseEntity<?> retrieve(@PathVariable String code) {
        SharedItem item = shareService.getSharedItem(code);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        if (item.getTextContent() != null) {
            Map<String, String> body = new HashMap<>();
            body.put("text", item.getTextContent());
            return ResponseEntity.ok(body);
        } else if (item.getFilePath() != null) {
            // Return the file with attachment disposition
            File file = new File(item.getFilePath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                    .body(file);
        }
        return ResponseEntity.notFound().build();
    }

}
