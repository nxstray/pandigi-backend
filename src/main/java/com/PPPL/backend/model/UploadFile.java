package com.PPPL.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String originalName;
    private String contentType;
    private long size;

    private LocalDateTime uploadedAt;
    
    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}
