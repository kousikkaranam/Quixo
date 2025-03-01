package org.quixo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    // Type: "text", "file", or "both"
    private String type;

    // Single field to store MongoDB reference(s).
    // For type "both", store as "textId:fileId"
    private String contentId;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
