package org.quixo.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "shared_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedFile {
    @Id
    private String id;  // MongoDB ObjectId as string
    private String code;
    private String fileName;
    private String contentType;
    private byte[] data;  // For smaller files; for large files consider GridFS
    private LocalDateTime createdAt;
}
