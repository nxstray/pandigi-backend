package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.admin.KaryawanDTO;
import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.service.admin.KaryawanService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/karyawan")
public class KaryawanController {

    @Autowired
    private KaryawanService karyawanService;

    /**
     * Get all karyawan
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<KaryawanDTO>>> getAllKaryawan() {
        List<KaryawanDTO> karyawan = karyawanService.getAllKaryawan();
        return ResponseEntity.ok(ApiResponse.success(karyawan));
    }

    /**
     * Search karyawan
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<KaryawanDTO>>> searchKaryawan(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer idManager) {
        List<KaryawanDTO> result = karyawanService.searchKaryawan(keyword, idManager);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Get karyawan by manager
     */
    @GetMapping("/manager/{idManager}")
    public ResponseEntity<ApiResponse<List<KaryawanDTO>>> getKaryawanByManager(
            @PathVariable Integer idManager) {
        List<KaryawanDTO> karyawan = karyawanService.getKaryawanByManager(idManager);
        return ResponseEntity.ok(ApiResponse.success(karyawan));
    }

    /**
     * Get karyawan by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KaryawanDTO>> getKaryawanById(@PathVariable Integer id) {
        KaryawanDTO karyawan = karyawanService.getKaryawanById(id);
        return ResponseEntity.ok(ApiResponse.success(karyawan));
    }

    /**
     * Create new karyawan
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<KaryawanDTO>> createKaryawan(
            @Valid @RequestBody KaryawanDTO dto) {
        KaryawanDTO created = karyawanService.createKaryawan(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Karyawan berhasil ditambahkan", created));
    }

    /**
     * Update karyawan
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<KaryawanDTO>> updateKaryawan(
            @PathVariable Integer id,
            @Valid @RequestBody KaryawanDTO dto) {
        KaryawanDTO updated = karyawanService.updateKaryawan(id, dto);
        return ResponseEntity.ok(
            ApiResponse.success("Karyawan berhasil diupdate", updated));
    }

    /**
     * Delete karyawan
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteKaryawan(@PathVariable Integer id) {
        karyawanService.deleteKaryawan(id);
        return ResponseEntity.ok(ApiResponse.success("Karyawan berhasil dihapus", null));
    }
}