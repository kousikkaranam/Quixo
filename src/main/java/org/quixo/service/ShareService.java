package org.quixo.service;

import org.quixo.dto.ShareRequest;
import org.quixo.model.SharedItem;
import org.quixo.repository.SharedItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ShareService {
    @Autowired
    private SharedItemRepository repository;

    public String saveSharedItem(ShareRequest request) throws IOException {
        String code = UUID.randomUUID().toString().substring(0, 8);
        SharedItem item = new SharedItem();
        item.setCode(code);
        item.setCreatedAt(LocalDateTime.now());
        if (request.getText() != null) {
            item.setTextContent(request.getText());
        }
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            String filePath = "uploads/" + code + "-" + request.getFile().getOriginalFilename();
            Files.copy(request.getFile().getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            item.setFilePath(filePath);
        }
        repository.save(item);
        return code;
    }

    public SharedItem getSharedItem(String code) {
        return repository.findByCode(code);
    }
}
