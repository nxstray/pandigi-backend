package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.admin.KaryawanDTO;
import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.service.admin.KaryawanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/karyawan")
@CrossOrigin(origins = "http://localhost:4200")
public class KaryawanController {
    
    @Autowired
    private KaryawanService karyawanService;
    
    /**
     * Get all karyawan
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<KaryawanDTO>>> getAllKaryawan() {
        try {
            List<KaryawanDTO> karyawan = karyawanService.getAllKaryawan();
            return ResponseEntity.ok(ApiResponse.success(karyawan));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat data karyawan: " + e.getMessage()));
        }
    }
    
    /**
     * Get karyawan by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KaryawanDTO>> getKaryawanById(@PathVariable Integer id) {
        try {
            KaryawanDTO karyawan = karyawanService.getKaryawanById(id);
            return ResponseEntity.ok(ApiResponse.success(karyawan));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Create new karyawan
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<KaryawanDTO>> createKaryawan(@RequestBody KaryawanDTO dto) {
        try {
            KaryawanDTO created = karyawanService.createKaryawan(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Karyawan berhasil ditambahkan", created));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal menambah karyawan: " + e.getMessage()));
        }
    }
    
    /**
     * Update karyawan
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<KaryawanDTO>> updateKaryawan(
            @PathVariable Integer id, 
            @RequestBody KaryawanDTO dto) {
        try {
            KaryawanDTO updated = karyawanService.updateKaryawan(id, dto);
            return ResponseEntity.ok(
                ApiResponse.success("Karyawan berhasil diupdate", updated));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal update karyawan: " + e.getMessage()));
        }
    }
    
    /**
     * Delete karyawan
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteKaryawan(@PathVariable Integer id) {
        try {
            karyawanService.deleteKaryawan(id);
            return ResponseEntity.ok(ApiResponse.success("Karyawan berhasil dihapus", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal hapus karyawan: " + e.getMessage()));
        }
    }
    
    /**
     * Search karyawan
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<KaryawanDTO>>> searchKaryawan(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer idManager) {
        try {
            List<KaryawanDTO> result = karyawanService.searchKaryawan(keyword, idManager);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal search karyawan: " + e.getMessage()));
        }
    }
    
    /**
     * Get karyawan by manager
     */
    @GetMapping("/manager/{idManager}")
    public ResponseEntity<ApiResponse<List<KaryawanDTO>>> getKaryawanByManager(@PathVariable Integer idManager) {
        try {
            List<KaryawanDTO> karyawan = karyawanService.getKaryawanByManager(idManager);
            return ResponseEntity.ok(ApiResponse.success(karyawan));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal load karyawan: " + e.getMessage()));
        }
    }
}