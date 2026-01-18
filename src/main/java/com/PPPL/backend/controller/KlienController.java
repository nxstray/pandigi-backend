package com.PPPL.backend.controller;

import com.PPPL.backend.data.ApiResponse;
import com.PPPL.backend.data.KlienDTO;
import com.PPPL.backend.model.Klien;
import com.PPPL.backend.model.Manager;
import com.PPPL.backend.model.StatusKlien;
import com.PPPL.backend.repository.KlienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/klien")
@CrossOrigin(origins = "http://localhost:4200")
public class KlienController {
    
    @Autowired
    private KlienRepository klienRepository;
    
    /**
     * Get all klien - filter out deleted ones
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<KlienDTO>>> getAllKlien() {
        List<KlienDTO> klien = klienRepository
            .findKlienYangTerverifikasi()
            .stream()
            .filter(k -> !Boolean.TRUE.equals(k.getIsDeleted())) // Filter deleted
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(klien));
    }
    
    /**
     * Get klien by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KlienDTO>> getKlienById(@PathVariable Integer id) {
        try {
            Klien klien = klienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Klien dengan ID " + id + " tidak ditemukan"));
            
            // Check if deleted
            if (Boolean.TRUE.equals(klien.getIsDeleted())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Klien tidak ditemukan"));
            }
            
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(klien)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Create new klien
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<KlienDTO>> createKlien(@RequestBody KlienDTO dto) {
        try {
            // Validasi
            if (dto.getNamaKlien() == null || dto.getNamaKlien().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Nama klien wajib diisi"));
            }
            
            if (dto.getEmailKlien() == null || dto.getEmailKlien().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email wajib diisi"));
            }
            
            // Check duplicate email
            if (klienRepository.findByEmailKlien(dto.getEmailKlien()).isPresent()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email sudah terdaftar"));
            }
            
            // Create entity
            Klien klien = new Klien();
            klien.setNamaKlien(dto.getNamaKlien());
            klien.setEmailKlien(dto.getEmailKlien());
            klien.setNoTelp(dto.getNoTelp());
            klien.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusKlien.BELUM);
            klien.setTglRequest(dto.getTglRequest() != null ? dto.getTglRequest() : new Date());
            klien.setIsDeleted(false); // Explicitly set
            
            Klien saved = klienRepository.save(klien);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Klien berhasil ditambahkan", convertToDTO(saved)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal menambah klien: " + e.getMessage()));
        }
    }
    
    /**
     * Update klien
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<KlienDTO>> updateKlien(
            @PathVariable Integer id, 
            @RequestBody KlienDTO dto) {
        try {
            Klien klien = klienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Klien dengan ID " + id + " tidak ditemukan"));
            
            // Check if deleted
            if (Boolean.TRUE.equals(klien.getIsDeleted())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Klien tidak dapat diupdate karena sudah dihapus"));
            }
            
            // Check duplicate email (exclude current)
            klienRepository.findByEmailKlien(dto.getEmailKlien()).ifPresent(existing -> {
                if (!existing.getIdKlien().equals(id)) {
                    throw new RuntimeException("Email sudah terdaftar");
                }
            });
            
            // Update fields
            klien.setNamaKlien(dto.getNamaKlien());
            klien.setEmailKlien(dto.getEmailKlien());
            klien.setNoTelp(dto.getNoTelp());
            if (dto.getStatus() != null) {
                klien.setStatus(dto.getStatus());
            }
            
            Klien updated = klienRepository.save(klien);
            
            return ResponseEntity.ok(
                ApiResponse.success("Klien berhasil diupdate", convertToDTO(updated)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal update klien: " + e.getMessage()));
        }
    }
    
    /**
     * Delete klien
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteKlien(@PathVariable Integer id) {
        try {
            // Find klien
            Klien klien = klienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Klien dengan ID " + id + " tidak ditemukan"));
            
            // Check if already deleted
            if (Boolean.TRUE.equals(klien.getIsDeleted())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Klien sudah dihapus sebelumnya"));
            }
            
            // Soft delete
            klien.setIsDeleted(true);
            klienRepository.save(klien);
            
            return ResponseEntity.ok(ApiResponse.success("Klien berhasil dihapus", null));
            
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Gagal hapus klien: " + errorMessage));
        }
    }
    
    /**
     * Search klien - filter deleted
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<KlienDTO>>> searchKlien(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) StatusKlien status) {
        try {
            List<Klien> klien = klienRepository.findAll().stream()
                .filter(k -> !Boolean.TRUE.equals(k.getIsDeleted())) // Filter deleted
                .collect(Collectors.toList());
            
            // Filter by keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.toLowerCase();
                klien = klien.stream()
                    .filter(k -> k.getNamaKlien().toLowerCase().contains(kw) ||
                               k.getEmailKlien().toLowerCase().contains(kw))
                    .collect(Collectors.toList());
            }
            
            // Filter by status
            if (status != null) {
                klien = klien.stream()
                    .filter(k -> k.getStatus() == status)
                    .collect(Collectors.toList());
            }
            
            List<KlienDTO> result = klien.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal search klien: " + e.getMessage()));
        }
    }
    
    /**
     * Update klien status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<KlienDTO>> updateKlienStatus(
            @PathVariable Integer id,
            @RequestParam StatusKlien status) {
        try {
            Klien klien = klienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Klien dengan ID " + id + " tidak ditemukan"));
            
            // Check if deleted
            if (Boolean.TRUE.equals(klien.getIsDeleted())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Klien tidak dapat diupdate karena sudah dihapus"));
            }
            
            klien.setStatus(status);
            Klien updated = klienRepository.save(klien);
            
            return ResponseEntity.ok(
                ApiResponse.success("Status klien berhasil diupdate", convertToDTO(updated)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal update status: " + e.getMessage()));
        }
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Convert Klien entity to DTO - dengan info approved by manager
     */
    private KlienDTO convertToDTO(Klien klien) {
        KlienDTO dto = new KlienDTO();
        dto.setIdKlien(klien.getIdKlien());
        dto.setNamaKlien(klien.getNamaKlien());
        dto.setEmailKlien(klien.getEmailKlien());
        dto.setNoTelp(klien.getNoTelp());
        dto.setStatus(klien.getStatus());
        dto.setTglRequest(klien.getTglRequest());
        
        // Get last approved manager
        if (klien.getManagerSet() != null && !klien.getManagerSet().isEmpty()) {
            Manager lastManager = klien.getManagerSet().stream()
                    .findFirst()
                    .orElse(null);
            
            if (lastManager != null) {
                dto.setLastApprovedBy(lastManager.getNamaManager());
            }
        }
        
        return dto;
    }
}