package org.quixo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ShareRequest {
    private String text;
    private MultipartFile file;
}
