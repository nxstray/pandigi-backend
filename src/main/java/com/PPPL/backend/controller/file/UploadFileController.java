package com.PPPL.backend.controller.file;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.model.file.UploadFile;
import com.PPPL.backend.service.file.UploadFileService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/upload")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class UploadFileController {

    private final UploadFileService uploadFileService;

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<UploadFile>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            UploadFile uploadedFile = uploadFileService.uploadFile(file);
            return ResponseEntity.ok(ApiResponse.success("Upload berhasil", uploadedFile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Gagal upload: " + e.getMessage()));
        }
    }
}
