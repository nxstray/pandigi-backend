package com.PPPL.backend.service;

import com.PPPL.backend.model.UploadFile;
import com.PPPL.backend.repository.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.io.File;

@Service
@RequiredArgsConstructor
public class UploadFileService {

    private final UploadFileRepository repository;

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            System.out.println("Upload directory initialized: " + uploadDir.getAbsolutePath());
        }
    }

    public UploadFile uploadFile(MultipartFile file) throws IOException {
        // Validasi
        if (file.isEmpty()) {
            throw new IOException("File kosong");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IOException("Ukuran file maksimal 5MB");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IOException("File harus berupa gambar");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + extension;

        // Ensure upload directory exists
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Save file
        Path filePath = uploadDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("File uploaded successfully: " + filePath.toAbsolutePath());

        // Save to database
        UploadFile uploadedFile = UploadFile.builder()
                .filename(uniqueFilename)
                .originalName(originalFilename)
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();

        return repository.save(uploadedFile);
    }
}