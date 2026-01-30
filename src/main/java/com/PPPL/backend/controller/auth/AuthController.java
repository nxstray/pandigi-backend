package com.PPPL.backend.controller.auth;

import com.PPPL.backend.data.admin.AdminDTO;
import com.PPPL.backend.data.auth.ChangePasswordRequest;
import com.PPPL.backend.data.auth.ForgotPasswordRequest;
import com.PPPL.backend.data.auth.LoginRequest;
import com.PPPL.backend.data.auth.LoginResponse;
import com.PPPL.backend.data.auth.ResetPasswordRequest;
import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.security.JwtUtil;
import com.PPPL.backend.service.auth.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Login endpoint (public)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login berhasil", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Upload foto profil
     */
    @PostMapping("/upload-photo")
    public ResponseEntity<ApiResponse<String>> uploadFotoProfil(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        try {
            String token = authHeader.substring(7);
            Integer idAdmin = jwtUtil.getAdminIdFromToken(token);
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("File tidak boleh kosong"));
            }
            
            // Validate file size (max 2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ukuran file maksimal 2MB"));
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("File harus berupa gambar"));
            }
            
            // Convert to Base64
            byte[] bytes = file.getBytes();
            String base64Image = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(bytes);
            
            // Save to database
            authService.updateFotoProfil(idAdmin, base64Image);
            
            return ResponseEntity.ok(ApiResponse.success("Foto profil berhasil diupload", base64Image));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal upload foto: " + e.getMessage()));
        }
    }
    
    /**
     * Get current user info
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AdminDTO>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            
            AdminDTO admin = authService.getAdminByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(admin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Token tidak valid"));
        }
    }
    
    /**
     * Ubah password
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordRequest request) {
        try {
            String token = authHeader.substring(7);
            Integer idAdmin = jwtUtil.getAdminIdFromToken(token);
            
            authService.changePassword(idAdmin, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("Password berhasil diubah", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Request password reset
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok(
                ApiResponse.success("Link reset password telah dikirim ke email Anda. Silakan cek inbox/spam.", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Reset password with token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(
                ApiResponse.success("Password berhasil direset. Silakan login dengan password baru Anda.", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Validasi token
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.validateToken(token);
            
            if (isValid) {
                return ResponseEntity.ok(ApiResponse.success(true));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token tidak valid"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Token tidak valid"));
        }
    }
    
    /**
     * Get all admins (hanya SUPER_ADMIN)
     */
    @GetMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminDTO>>> getAllAdmins() {
        List<AdminDTO> admins = authService.getAllAdmins();
        return ResponseEntity.ok(ApiResponse.success(admins));
    }
    
    /**
     * Deactivate admin (hanya SUPER_ADMIN)
     */
    @PutMapping("/admins/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateAdmin(@PathVariable Integer id) {
        try {
            authService.deactivateAdmin(id);
            return ResponseEntity.ok(ApiResponse.success("Admin berhasil dinonaktifkan", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Activate admin (hanya SUPER_ADMIN)
     */
    @PutMapping("/admins/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activateAdmin(@PathVariable Integer id) {
        try {
            authService.activateAdmin(id);
            return ResponseEntity.ok(ApiResponse.success("Admin berhasil diaktifkan", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}