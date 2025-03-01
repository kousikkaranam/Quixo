package org.quixo.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "shared_texts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedText {
    @Id
    private String id;  // MongoDB ObjectId as string
    private String code;
    private String content;
    private LocalDateTime createdAt;
}
