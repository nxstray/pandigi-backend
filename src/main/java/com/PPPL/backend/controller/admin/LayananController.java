package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.data.layanan.LayananDTO;
import com.PPPL.backend.model.enums.KategoriLayanan;
import com.PPPL.backend.service.admin.LayananService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/layanan")
public class LayananController {

    @Autowired
    private LayananService layananService;

    // Get all layanan
    @GetMapping
    public ResponseEntity<ApiResponse<List<LayananDTO>>> getAllLayanan() {
        return ResponseEntity.ok(ApiResponse.success(layananService.getAllLayanan()));
    }

    // Search layanan by keyword &/ kategori
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LayananDTO>>> searchLayanan(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) KategoriLayanan kategori) {
        return ResponseEntity.ok(ApiResponse.success(
            layananService.searchLayanan(keyword, kategori)));
    }

    // Get layanan by kategori
    @GetMapping("/kategori/{kategori}")
    public ResponseEntity<ApiResponse<List<LayananDTO>>> getLayananByKategori(
            @PathVariable KategoriLayanan kategori) {
        return ResponseEntity.ok(ApiResponse.success(
            layananService.getLayananByKategori(kategori)));
    }

    // Get layanan by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LayananDTO>> getLayananById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(layananService.getLayananById(id)));
    }

    // Create new layanan
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<LayananDTO>> createLayanan(
            @Valid @RequestBody LayananDTO dto) {
        LayananDTO created = layananService.createLayanan(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Layanan berhasil ditambahkan", created));
    }

    // Update layanan by ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<LayananDTO>> updateLayanan(
            @PathVariable Integer id,
            @Valid @RequestBody LayananDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(
            "Layanan berhasil diupdate", layananService.updateLayanan(id, dto)));
    }

    // Delete layanan by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLayanan(@PathVariable Integer id) {
        layananService.deleteLayanan(id);
        return ResponseEntity.ok(ApiResponse.success("Layanan berhasil dihapus", null));
    }
}