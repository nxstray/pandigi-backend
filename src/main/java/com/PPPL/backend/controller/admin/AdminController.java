package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.admin.AdminDTO;
import com.PPPL.backend.data.admin.RegisterManagerRequest;
import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.service.admin.AdminService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Register new manager
     * Auto-generate username & password, send via email
     */
    @PostMapping("/register-manager")
    public ResponseEntity<ApiResponse<AdminDTO>> registerManager(
            @Valid @RequestBody RegisterManagerRequest request) {
        AdminDTO manager = adminService.registerManager(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Manager berhasil didaftarkan. Email dengan kredensial telah dikirim.",
                manager
            ));
    }
}