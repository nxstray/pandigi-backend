package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.client.KlienDTO;
import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.model.enums.StatusKlien;
import com.PPPL.backend.service.admin.KlienService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/klien")
@CrossOrigin(origins = "http://localhost:4200")
public class KlienController {
    
    @Autowired
    private KlienService klienService;
    
    /**
     * Get all klien - filter out deleted ones
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<KlienDTO>>> getAllKlien() {
        List<KlienDTO> klien = klienService.getAllKlien();
        return ResponseEntity.ok(ApiResponse.success(klien));
    }
    
    /**
     * Get klien by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KlienDTO>> getKlienById(@PathVariable Integer id) {
        try {
            KlienDTO klien = klienService.getKlienById(id);
            return ResponseEntity.ok(ApiResponse.success(klien));
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
            KlienDTO created = klienService.createKlien(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Klien berhasil ditambahkan", created));
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
            KlienDTO updated = klienService.updateKlien(id, dto);
            return ResponseEntity.ok(
                ApiResponse.success("Klien berhasil diupdate", updated));
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
            klienService.deleteKlien(id);
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
            List<KlienDTO> result = klienService.searchKlien(keyword, status);
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
            KlienDTO updated = klienService.updateKlienStatus(id, status);
            return ResponseEntity.ok(
                ApiResponse.success("Status klien berhasil diupdate", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal update status: " + e.getMessage()));
        }
    }
}