package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.data.rekap.RekapDTO;
import com.PPPL.backend.model.enums.StatusRekap;
import com.PPPL.backend.security.AuthUser;
import com.PPPL.backend.service.rekap.RekapService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rekap")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
public class RekapController {

    @Autowired
    private RekapService rekapService;

    // Get all rekap
    @GetMapping
    public ResponseEntity<ApiResponse<List<RekapDTO>>> getAllRekap() {
        return ResponseEntity.ok(ApiResponse.success(rekapService.getAllRekap()));
    }

    // Search rekap by keyword &/ status
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RekapDTO>>> searchRekap(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) StatusRekap status) {
        return ResponseEntity.ok(ApiResponse.success(
            rekapService.searchRekap(keyword, status)));
    }

    // Get rekap by klien ID
    @GetMapping("/klien/{idKlien}")
    public ResponseEntity<ApiResponse<List<RekapDTO>>> getRekapByKlien(
            @PathVariable Integer idKlien) {
        return ResponseEntity.ok(ApiResponse.success(
            rekapService.getRekapByKlien(idKlien)));
    }

    // Get rekap by manager ID
    @GetMapping("/manager/{idManager}")
    public ResponseEntity<ApiResponse<List<RekapDTO>>> getRekapByManager(
            @PathVariable Integer idManager) {
        return ResponseEntity.ok(ApiResponse.success(
            rekapService.getRekapByManager(idManager)));
    }

    // Get rekap by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RekapDTO>> getRekapById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(rekapService.getRekapById(id)));
    }

    // Create new rekap
    @PostMapping
    public ResponseEntity<ApiResponse<RekapDTO>> createRekap(
            @Valid @RequestBody RekapDTO dto) {
        RekapDTO created = rekapService.createRekap(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Rekap meeting berhasil ditambahkan", created));
    }

    // Update rekap by ID
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RekapDTO>> updateRekap(
            @PathVariable Integer id,
            @Valid @RequestBody RekapDTO dto) {
        AuthUser auth = AuthUser.fromContext();
        RekapDTO updated = rekapService.updateRekapDTO(id, dto, auth);
        return ResponseEntity.ok(
            ApiResponse.success("Rekap meeting berhasil diupdate", updated));
    }

    // Delete rekap by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRekap(@PathVariable Integer id) {
        rekapService.deleteRekap(id);
        return ResponseEntity.ok(ApiResponse.success("Rekap meeting berhasil dihapus", null));
    }
}