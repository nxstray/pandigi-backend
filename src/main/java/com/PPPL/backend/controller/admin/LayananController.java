package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.data.layanan.LayananDTO;
import com.PPPL.backend.model.enums.KategoriLayanan;
import com.PPPL.backend.service.admin.LayananService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/layanan")
@CrossOrigin(origins = "http://localhost:4200")
public class LayananController {
    
    @Autowired
    private LayananService layananService;
    
    /**
     * Get all layanan
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LayananDTO>>> getAllLayanan() {
        try {
            List<LayananDTO> layanan = layananService.getAllLayanan();
            return ResponseEntity.ok(ApiResponse.success(layanan));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat data layanan: " + e.getMessage()));
        }
    }
    
    /**
     * Get layanan by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LayananDTO>> getLayananById(@PathVariable Integer id) {
        try {
            LayananDTO layanan = layananService.getLayananById(id);
            return ResponseEntity.ok(ApiResponse.success(layanan));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Create new layanan
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<LayananDTO>> createLayanan(@RequestBody LayananDTO dto) {
        try {
            LayananDTO created = layananService.createLayanan(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Layanan berhasil ditambahkan", created));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal menambah layanan: " + e.getMessage()));
        }
    }
    
    /**
     * Update layanan
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<LayananDTO>> updateLayanan(
            @PathVariable Integer id, 
            @RequestBody LayananDTO dto) {
        try {
            LayananDTO updated = layananService.updateLayanan(id, dto);
            return ResponseEntity.ok(
                ApiResponse.success("Layanan berhasil diupdate", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal update layanan: " + e.getMessage()));
        }
    }
    
    /**
     * Delete layanan
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLayanan(@PathVariable Integer id) {
        try {
            layananService.deleteLayanan(id);
            return ResponseEntity.ok(ApiResponse.success("Layanan berhasil dihapus", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal hapus layanan: " + e.getMessage()));
        }
    }
    
    /**
     * Search layanan
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LayananDTO>>> searchLayanan(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) KategoriLayanan kategori) {
        try {
            List<LayananDTO> result = layananService.searchLayanan(keyword, kategori);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal search layanan: " + e.getMessage()));
        }
    }
    
    /**
     * Get layanan by kategori
     */
    @GetMapping("/kategori/{kategori}")
    public ResponseEntity<ApiResponse<List<LayananDTO>>> getLayananByKategori(
            @PathVariable KategoriLayanan kategori) {
        try {
            List<LayananDTO> layanan = layananService.getLayananByKategori(kategori);
            return ResponseEntity.ok(ApiResponse.success(layanan));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal load layanan: " + e.getMessage()));
        }
    }
}