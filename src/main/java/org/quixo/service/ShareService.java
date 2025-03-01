package org.quixo.service;

import org.quixo.model.SharedItem;
import org.quixo.repository.SharedItemRepository;
import org.quixo.dto.ShareRequest;
import org.quixo.mongo.SharedText;
import org.quixo.mongo.SharedFile;
import org.quixo.mongo.SharedTextRepository;
import org.quixo.mongo.SharedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ShareService {

    @Autowired
    private SharedItemRepository repository;

    @Autowired
    private SharedTextRepository textRepository;

    @Autowired
    private SharedFileRepository fileRepository;

    public String saveSharedItem(ShareRequest request) throws IOException {
        String code = UUID.randomUUID().toString().substring(0, 8);
        SharedItem item = new SharedItem();
        item.setCode(code);
        item.setCreatedAt(LocalDateTime.now());

        boolean hasText = request.getText() != null && !request.getText().trim().isEmpty();
        boolean hasFile = request.getFile() != null && !request.getFile().isEmpty();

        String type = "";
        if (hasText && hasFile) {
            type = "BOTH";
        } else if (hasText) {
            type = "TEXT";
        } else if (hasFile) {
            type = "FILE";
        }
        item.setType(type);

        if (hasText && !hasFile) {
            SharedText sharedText = new SharedText();
            sharedText.setCode(code);
            sharedText.setContent(request.getText());
            sharedText.setCreatedAt(LocalDateTime.now());
            SharedText savedText = textRepository.save(sharedText);
            item.setContentId(savedText.getId());
        } else if (!hasText && hasFile) {
            MultipartFile file = request.getFile();
            SharedFile sharedFile = new SharedFile();
            sharedFile.setCode(code);
            sharedFile.setFileName(file.getOriginalFilename());
            sharedFile.setContentType(file.getContentType());
            sharedFile.setData(file.getBytes());
            sharedFile.setCreatedAt(LocalDateTime.now());
            SharedFile savedFile = fileRepository.save(sharedFile);
            item.setContentId(savedFile.getId());
        } else if (hasText && hasFile) {
            // Save both separately and combine IDs with colon as delimiter
            SharedText sharedText = new SharedText();
            sharedText.setCode(code);
            sharedText.setContent(request.getText());
            sharedText.setCreatedAt(LocalDateTime.now());
            SharedText savedText = textRepository.save(sharedText);

            MultipartFile file = request.getFile();
            SharedFile sharedFile = new SharedFile();
            sharedFile.setCode(code);
            sharedFile.setFileName(file.getOriginalFilename());
            sharedFile.setContentType(file.getContentType());
            sharedFile.setData(file.getBytes());
            sharedFile.setCreatedAt(LocalDateTime.now());
            SharedFile savedFile = fileRepository.save(sharedFile);

            // Combine the two IDs separated by a colon.
            item.setContentId(savedText.getId() + ":" + savedFile.getId());
        }

        repository.save(item);
        return code;
    }
}
