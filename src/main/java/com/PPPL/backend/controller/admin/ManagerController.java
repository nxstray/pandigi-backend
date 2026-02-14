package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.admin.ManagerDTO;
import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.service.admin.ManagerService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    // Get all managers
    @GetMapping
    public ResponseEntity<ApiResponse<List<ManagerDTO>>> getAllManagers() {
        return ResponseEntity.ok(ApiResponse.success(managerService.getAllManagers()));
    }

    // Search managers by keyword &/ divisi
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ManagerDTO>>> searchManagers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String divisi) {
        return ResponseEntity.ok(ApiResponse.success(
            managerService.searchManagers(keyword, divisi)));
    }

    // Get list of divisi
    @GetMapping("/divisi")
    public ResponseEntity<ApiResponse<List<String>>> getDivisiList() {
        return ResponseEntity.ok(ApiResponse.success(managerService.getDivisiList()));
    }

    // Get divisi from layanan
    @GetMapping("/divisi-layanan")
    public ResponseEntity<ApiResponse<List<String>>> getDivisiFromLayanan() {
        return ResponseEntity.ok(ApiResponse.success(managerService.getDivisiFromLayanan()));
    }

    // Get manager by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ManagerDTO>> getManagerById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(managerService.getManagerById(id)));
    }

    // Create new manager
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ManagerDTO>> createManager(
            @Valid @RequestBody ManagerDTO dto) {
        ManagerDTO created = managerService.createManager(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Manager berhasil ditambahkan", created));
    }

    // Update manager by ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ManagerDTO>> updateManager(
            @PathVariable Integer id,
            @Valid @RequestBody ManagerDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(
            "Manager berhasil diupdate", managerService.updateManager(id, dto)));
    }

    // Delete manager by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteManager(@PathVariable Integer id) {
        managerService.deleteManager(id);
        return ResponseEntity.ok(
            ApiResponse.success("Manager dan akun login berhasil dihapus", null));
    }
}