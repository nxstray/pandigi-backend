package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.client.KlienDTO;
import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.model.enums.StatusKlien;
import com.PPPL.backend.service.admin.KlienService;

import jakarta.validation.Valid;
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

    // Get all klien
    @GetMapping
    public ResponseEntity<ApiResponse<List<KlienDTO>>> getAllKlien() {
        return ResponseEntity.ok(ApiResponse.success(klienService.getAllKlien()));
    }

    // Search klien by keyword &/ status
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<KlienDTO>>> searchKlien(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) StatusKlien status) {
        return ResponseEntity.ok(ApiResponse.success(
            klienService.searchKlien(keyword, status)));
    }

    // Get klien by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KlienDTO>> getKlienById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(klienService.getKlienById(id)));
    }

    // Create new klien
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<KlienDTO>> createKlien(
            @Valid @RequestBody KlienDTO dto) {
        KlienDTO created = klienService.createKlien(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Klien berhasil ditambahkan", created));
    }

    // Update klien by ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<KlienDTO>> updateKlien(
            @PathVariable Integer id,
            @Valid @RequestBody KlienDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(
            "Klien berhasil diupdate", klienService.updateKlien(id, dto)));
    }

    // Delete klien by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteKlien(@PathVariable Integer id) {
        klienService.deleteKlien(id);
        return ResponseEntity.ok(ApiResponse.success("Klien berhasil dihapus", null));
    }

    // Update klien status by ID
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<KlienDTO>> updateKlienStatus(
            @PathVariable Integer id,
            @RequestParam StatusKlien status) {
        return ResponseEntity.ok(ApiResponse.success(
            "Status klien berhasil diupdate", klienService.updateKlienStatus(id, status)));
    }
}