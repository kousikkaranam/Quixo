package org.quixo.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Entity
public class SharedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;


    @Column(columnDefinition = "TEXT")
    private String textContent;
    private String filePath;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}

