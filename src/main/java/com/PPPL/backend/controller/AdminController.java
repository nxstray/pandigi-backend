package com.PPPL.backend.controller;

import com.PPPL.backend.data.admin.AdminDTO;
import com.PPPL.backend.data.admin.RegisterManagerRequest;
import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * Register manager baru (hanya SUPER_ADMIN yang bisa register)
     * Auto-generate username & password, kirim via email
     */
    @PostMapping("/register-manager")
    public ResponseEntity<ApiResponse<AdminDTO>> registerManager(@RequestBody RegisterManagerRequest request) {
        try {
            AdminDTO manager = adminService.registerManager(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    "Manager berhasil didaftarkan. Email dengan kredensial telah dikirim.", 
                    manager
                ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.toString()));
        }
    }
}