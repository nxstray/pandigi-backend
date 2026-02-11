package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.admin.ManagerDTO;
import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.service.admin.ManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/manager")
@CrossOrigin(origins = "http://localhost:4200")
public class ManagerController {
    
    @Autowired
    private ManagerService managerService;
    
    /**
     * Get all managers
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ManagerDTO>>> getAllManagers() {
        try {
            List<ManagerDTO> managers = managerService.getAllManagers();
            return ResponseEntity.ok(ApiResponse.success(managers));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat data manager: " + e.getMessage()));
        }
    }
    
    /**
     * Get manager by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ManagerDTO>> getManagerById(@PathVariable Integer id) {
        try {
            ManagerDTO manager = managerService.getManagerById(id);
            return ResponseEntity.ok(ApiResponse.success(manager));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Create new manager
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ManagerDTO>> createManager(@RequestBody ManagerDTO dto) {
        try {
            ManagerDTO created = managerService.createManager(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Manager berhasil ditambahkan", created));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal menambah manager: " + e.getMessage()));
        }
    }
    
    /**
     * Update manager
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ManagerDTO>> updateManager(
            @PathVariable Integer id, 
            @RequestBody ManagerDTO dto) {
        try {
            ManagerDTO updated = managerService.updateManager(id, dto);
            return ResponseEntity.ok(
                ApiResponse.success("Manager berhasil diupdate", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal update manager: " + e.getMessage()));
        }
    }
    
    /**
     * Delete manager - HARD DELETE from both table (Manager & Admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteManager(@PathVariable Integer id) {
        try {
            managerService.deleteManager(id);
            return ResponseEntity.ok(ApiResponse.success("Manager dan akun login berhasil dihapus", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal hapus manager: " + e.getMessage()));
        }
    }
    
    /**
     * Search managers by name or divisi
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ManagerDTO>>> searchManagers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String divisi) {
        try {
            List<ManagerDTO> result = managerService.searchManagers(keyword, divisi);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal search manager: " + e.getMessage()));
        }
    }
    
    /**
     * Get unique divisi list
     */
    @GetMapping("/divisi")
    public ResponseEntity<ApiResponse<List<String>>> getDivisiList() {
        try {
            List<String> divisiList = managerService.getDivisiList();
            return ResponseEntity.ok(ApiResponse.success(divisiList));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal load divisi: " + e.getMessage()));
        }
    }

    /**
     * Get divisi list from nama layanan
     */
    @GetMapping("/divisi-layanan")
    public ResponseEntity<ApiResponse<List<String>>> getDivisiFromLayanan() {
        try {
            List<String> divisiList = managerService.getDivisiFromLayanan();
            return ResponseEntity.ok(ApiResponse.success(divisiList));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal load divisi: " + e.getMessage()));
        }
    }
}