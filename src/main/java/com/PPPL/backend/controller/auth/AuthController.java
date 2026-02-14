package com.PPPL.backend.controller.auth;

import com.PPPL.backend.config.cache.RateLimiterRedisConfig;
import com.PPPL.backend.data.admin.AdminDTO;
import com.PPPL.backend.data.auth.ChangePasswordRequest;
import com.PPPL.backend.data.auth.ForgotPasswordRequest;
import com.PPPL.backend.data.auth.LoginRequest;
import com.PPPL.backend.data.auth.LoginResponse;
import com.PPPL.backend.data.auth.ResetPasswordRequest;
import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.handler.RateLimitExceededException;
import com.PPPL.backend.handler.ResourceNotFoundException;
import com.PPPL.backend.security.JwtUtil;
import com.PPPL.backend.service.auth.AuthService;
import com.PPPL.backend.validator.FileUploadValidator;

import jakarta.servlet.http.HttpServletRequest;

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
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FileUploadValidator fileUploadValidator;

    @Autowired
    private RateLimiterRedisConfig rateLimiterRedisConfig;

    // Authentication Endpoints
    /**
     * Login - rate limited 5x per 15 menit per IP
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIp(httpRequest);

        if (!rateLimiterRedisConfig.allowAuthAttempt(ipAddress)) {
            long waitSeconds = rateLimiterRedisConfig.getAuthTimeUntilReset(ipAddress);
            throw new RateLimitExceededException(
                "Terlalu banyak percobaan login. Coba lagi dalam " + waitSeconds + " detik.",
                waitSeconds
            );
        }

        try {
            LoginResponse response = authService.login(request);
            rateLimiterRedisConfig.clearAuthRateLimit(ipAddress);
            return ResponseEntity.ok(ApiResponse.success("Login berhasil", response));
        } catch (RuntimeException e) {
            // Login Failed, increment rate limit counter
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Upload foto profil admin
     */
    @PostMapping("/upload-photo")
    public ResponseEntity<ApiResponse<String>> uploadFotoProfil(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        try {
            String token = authHeader.substring(7);
            Integer idAdmin = jwtUtil.getAdminIdFromToken(token);

            fileUploadValidator.validate(file);

            byte[] bytes = file.getBytes();
            String base64Image = "data:" + file.getContentType() + ";base64,"
                + Base64.getEncoder().encodeToString(bytes);

            authService.updateFotoProfil(idAdmin, base64Image);
            return ResponseEntity.ok(
                ApiResponse.success("Foto profil berhasil diupload", base64Image));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Gagal upload foto: " + e.getMessage()));
        }
    }

    /**
     * Get current user info from token
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
     * Change password
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
        } catch (IllegalArgumentException e) {
            // Wrong old password or weak new password
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Forgot password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIp(httpRequest);

        if (!rateLimiterRedisConfig.allowAuthAttempt(ipAddress)) {
            long waitSeconds = rateLimiterRedisConfig.getAuthTimeUntilReset(ipAddress);
            throw new RateLimitExceededException(
                "Terlalu banyak percobaan. Coba lagi dalam " + waitSeconds + " detik.",
                waitSeconds
            );
        }

        // Always respond with success message to prevent email enumeration
        authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(
            ApiResponse.success(
                "Link reset password telah dikirim ke email Anda. Silakan cek inbox/spam.", null)
        );
    }

    /**
     * Reset password with token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Password berhasil direset. Silakan login dengan password baru Anda.", null)
            );
        } catch (IllegalArgumentException e) {
            // Token invalid, expired, atau password lemah
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Validate token JWT
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token tidak valid"));
            }

            String username = jwtUtil.getUsernameFromToken(token);
            authService.getAdminByUsername(username);

            return ResponseEntity.ok(ApiResponse.success("Token valid", true));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("User tidak ditemukan"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Token tidak valid"));
        }
    }

    // Admin Management Endpoints
    @GetMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminDTO>>> getAllAdmins() {
        List<AdminDTO> admins = authService.getAllAdmins();
        return ResponseEntity.ok(ApiResponse.success(admins));
    }

    @PutMapping("/admins/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateAdmin(@PathVariable Integer id) {
        authService.deactivateAdmin(id);
        return ResponseEntity.ok(ApiResponse.success("Admin berhasil dinonaktifkan", null));
    }

    @PutMapping("/admins/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activateAdmin(@PathVariable Integer id) {
        authService.activateAdmin(id);
        return ResponseEntity.ok(ApiResponse.success("Admin berhasil diaktifkan", null));
    }

    // Private helper methods
    /**
     * Get real IP address, handle reverse proxy / load balancer
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}