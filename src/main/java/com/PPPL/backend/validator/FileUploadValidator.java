package com.PPPL.backend.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class FileUploadValidator {
    
    private static final Logger log = LoggerFactory.getLogger(FileUploadValidator.class);
    
    // Allowed file extensions
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "jpg", "jpeg", "png", "webp"
    );
    
    // Allowed MIME types
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "image/jpeg", 
        "image/png", 
        "image/webp"
    );
    
    // Maximum file size (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    // File signatures for validation (magic bytes)
    private static final byte[] JPEG_SIGNATURE = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final byte[] WEBP_SIGNATURE = new byte[]{0x52, 0x49, 0x46, 0x46};
    
    /**
     * Validate uploaded file for security
     */
    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File tidak boleh kosong");
        }
        
        // 1. Validate file size
        validateFileSize(file);
        
        // 2. Validate file extension
        validateFileExtension(file);
        
        // 3. Validate MIME type
        validateMimeType(file);
        
        // 4. Validate file signature (magic bytes) - prevents fake extensions
        validateFileSignature(file);
        
        log.info("File validation successful: {} ({})", 
            file.getOriginalFilename(), formatFileSize(file.getSize()));
    }
    
    /**
     * Validate file size
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                String.format(
                    "Ukuran file terlalu besar. Maksimal %s, ukuran file Anda: %s",
                    formatFileSize(MAX_FILE_SIZE),
                    formatFileSize(file.getSize())
                )
            );
        }
        
        if (file.getSize() == 0) {
            throw new IllegalArgumentException("File kosong (0 bytes)");
        }
    }
    
    /**
     * Validate file extension
     */
    private void validateFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Nama file tidak valid");
        }
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                String.format(
                    "Tipe file tidak diizinkan. Hanya: %s. File Anda: .%s",
                    String.join(", ", ALLOWED_EXTENSIONS),
                    extension
                )
            );
        }
    }
    
    /**
     * Validate MIME type
     */
    private void validateMimeType(MultipartFile file) {
        String contentType = file.getContentType();
        
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                String.format(
                    "MIME type tidak valid. Hanya: %s. File Anda: %s",
                    String.join(", ", ALLOWED_MIME_TYPES),
                    contentType != null ? contentType : "unknown"
                )
            );
        }
    }
    
    /**
     * Validate file signature (magic bytes) to prevent fake extensions
     */
    private void validateFileSignature(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[12];
            int bytesRead = is.read(header);

            if (bytesRead < 4) {
            throw new IllegalArgumentException("File terlalu kecil atau rusak");
            }

            // Igonre validate WebP if bytes < 12
            boolean isValidSignature =
            isJpeg(header) ||
            isPng(header) ||
            (bytesRead >= 12 && isWebP(header));

            if (!isValidSignature) {
            throw new IllegalArgumentException(
                "File signature tidak valid. File mungkin bukan gambar asli."
            );
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Gagal membaca file. File mungkin rusak.");
        }
    }
    
    /**
     * Check if file is JPEG based on magic bytes
     */
    private boolean isJpeg(byte[] header) {
        return header[0] == JPEG_SIGNATURE[0] && 
               header[1] == JPEG_SIGNATURE[1] && 
               header[2] == JPEG_SIGNATURE[2];
    }
    
    /**
     * Check if file is PNG based on magic bytes
     */
    private boolean isPng(byte[] header) {
        return header[0] == PNG_SIGNATURE[0] && 
               header[1] == PNG_SIGNATURE[1] && 
               header[2] == PNG_SIGNATURE[2] && 
               header[3] == PNG_SIGNATURE[3];
    }
    
    /**
     * Check if file is WebP based on magic bytes
     */
    private boolean isWebP(byte[] header) {
        // WebP signature: RIFF....WEBP
        return header[0] == WEBP_SIGNATURE[0] && 
               header[1] == WEBP_SIGNATURE[1] && 
               header[2] == WEBP_SIGNATURE[2] && 
               header[3] == WEBP_SIGNATURE[3] &&
               header[8] == 'W' && 
               header[9] == 'E' && 
               header[10] == 'B' && 
               header[11] == 'P';
    }
    
    /**
     * Format file size for user-friendly display
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " bytes";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }
}