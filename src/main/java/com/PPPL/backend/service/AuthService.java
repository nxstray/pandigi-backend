package com.PPPL.backend.service;

import com.PPPL.backend.data.AdminDTO;
import com.PPPL.backend.data.LoginRequest;
import com.PPPL.backend.data.LoginResponse;
import com.PPPL.backend.model.Admin;
import com.PPPL.backend.handler.ResourceNotFoundException;
import com.PPPL.backend.repository.AdminRepository;
import com.PPPL.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;
    
    /**
     * Login admin - UPDATED to include isFirstLogin
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("Username atau password salah"));
        
        if (!admin.getIsActive()) {
            throw new RuntimeException("Akun Anda telah dinonaktifkan. Hubungi super admin.");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Username atau password salah");
        }
        
        admin.setLastLogin(new Date());
        adminRepository.save(admin);
        
        String token = jwtUtil.generateToken(
            admin.getUsername(), 
            admin.getIdAdmin(), 
            admin.getRole().name()
        );
        
        return new LoginResponse(
            token,
            admin.getIdAdmin(),
            admin.getUsername(),
            admin.getNamaLengkap(),
            admin.getEmail(),
            admin.getRole(),
            admin.getFotoProfil(),
            admin.getIsFirstLogin()
        );
    }
    
    /**
     * Update foto profil
     */
    @Transactional
    public void updateFotoProfil(Integer idAdmin, String fotoProfil) {
        Admin admin = adminRepository.findById(idAdmin)
            .orElseThrow(() -> new ResourceNotFoundException("Admin tidak ditemukan"));
        admin.setFotoProfil(fotoProfil);
        adminRepository.save(admin);
    }
    
    /**
     * Change password - UPDATED to reset isFirstLogin flag
     */
    @Transactional
    public void changePassword(Integer idAdmin, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findById(idAdmin)
            .orElseThrow(() -> new ResourceNotFoundException("Admin tidak ditemukan"));
        
        // Verify old password only if NOT first login
        if (!admin.getIsFirstLogin()) {
            if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
                throw new RuntimeException("Password lama tidak sesuai");
            }
        }
        
        // Validate new password strength
        validatePasswordStrength(newPassword);
        
        // Update password and reset first login flag
        admin.setPassword(passwordEncoder.encode(newPassword));
        admin.setIsFirstLogin(false);
        adminRepository.save(admin);
    }
    
    /**
     * Validate password strength
     */
    private void validatePasswordStrength(String password) {
        if (password.length() < 8) {
            throw new RuntimeException("Password minimal 8 karakter");
        }
        
        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException("Password harus mengandung minimal 1 huruf besar");
        }
        
        if (!password.matches(".*[a-z].*")) {
            throw new RuntimeException("Password harus mengandung minimal 1 huruf kecil");
        }
        
        if (!password.matches(".*\\d.*")) {
            throw new RuntimeException("Password harus mengandung minimal 1 angka");
        }
        
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new RuntimeException("Password harus mengandung minimal 1 karakter spesial");
        }
    }
    
    /**
     * Get admin by username
     */
    public AdminDTO getAdminByUsername(String username) {
        Admin admin = adminRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Admin tidak ditemukan"));
        return mapToDTO(admin);
    }
    
    /**
     * Get all admins
     */
    public List<AdminDTO> getAllAdmins() {
        return adminRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Deactivate admin
     */
    @Transactional
    public void deactivateAdmin(Integer idAdmin) {
        Admin admin = adminRepository.findById(idAdmin)
            .orElseThrow(() -> new ResourceNotFoundException("Admin tidak ditemukan"));
        admin.setIsActive(false);
        adminRepository.save(admin);
    }
    
    /**
     * Activate admin
     */
    @Transactional
    public void activateAdmin(Integer idAdmin) {
        Admin admin = adminRepository.findById(idAdmin)
            .orElseThrow(() -> new ResourceNotFoundException("Admin tidak ditemukan"));
        admin.setIsActive(true);
        adminRepository.save(admin);
    }

    /**
     * Request forgot password - Generate token and send email
     */
    @Transactional
    public void requestPasswordReset(String email) {
        Admin admin = adminRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email tidak terdaftar"));
        
        if (!admin.getIsActive()) {
            throw new RuntimeException("Akun tidak aktif. Hubungi administrator.");
        }
        
        // Generate secure random token
        String resetToken = generateResetToken();
        
        // Set token expiry (1 hour from now)
        Date expiry = new Date(System.currentTimeMillis() + 3600000); // 1 hour
        
        admin.setResetToken(resetToken);
        admin.setResetTokenExpiry(expiry);
        adminRepository.save(admin);
        
        // Send reset email
        sendPasswordResetEmail(admin, resetToken);
    }

    /**
     * Reset password using token
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        Admin admin = adminRepository.findByResetToken(token)
            .orElseThrow(() -> new RuntimeException("Token reset tidak valid"));
        
        // Check if token is expired
        if (admin.getResetTokenExpiry() == null || 
            admin.getResetTokenExpiry().before(new Date())) {
            throw new RuntimeException("Token reset sudah kadaluarsa. Silakan request ulang.");
        }
        
        // Validate new password
        validatePasswordStrength(newPassword);
        
        // Update password
        admin.setPassword(passwordEncoder.encode(newPassword));
        
        // Clear reset token
        admin.setResetToken(null);
        admin.setResetTokenExpiry(null);
        
        // Reset first login flag if needed
        admin.setIsFirstLogin(false);
        
        adminRepository.save(admin);
    }

    /**
     * Generate secure random reset token
     */
    private String generateResetToken() {
        return java.util.UUID.randomUUID().toString().replace("-", "") + 
            System.currentTimeMillis();
    }

    /**
     * Send password reset email
     */
    private void sendPasswordResetEmail(Admin admin, String resetToken) {
        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
        
        String subject = "Reset Password - PT. Pandawa Digital Mandiri";
        
        String htmlContent = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .container {
                        background: linear-gradient(135deg, #0C2B40 0%%, #1F4E79 100%%);
                        padding: 40px;
                        border-radius: 10px;
                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                    }
                    .content {
                        background: white;
                        padding: 30px;
                        border-radius: 8px;
                    }
                    .logo {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .logo h1 {
                        color: #0C2B40;
                        margin: 0;
                        font-size: 24px;
                    }
                    .warning {
                        background: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .btn {
                        display: inline-block;
                        background: #0C2B40;
                        color: #ffffff !important;
                        padding: 15px 40px;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 20px 0;
                        font-weight: bold;
                        font-size: 16px;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #e2e8f0;
                        color: #718096;
                        font-size: 14px;
                    }
                    .token-info {
                        background: #f7fafc;
                        padding: 15px;
                        border-radius: 4px;
                        margin: 15px 0;
                        font-size: 13px;
                        color: #666;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="content">
                        <div class="logo">
                            <h1>PT. Pandawa Digital Mandiri</h1>
                        </div>
                        
                        <h2 style="color: #2d3748;">Reset Password</h2>
                        
                        <p>Halo <strong>%s</strong>,</p>
                        
                        <p>Kami menerima permintaan untuk reset password akun Anda. Klik tombol di bawah ini untuk membuat password baru:</p>
                        
                        <center>
                            <a href="%s" class="btn">Reset Password</a>
                        </center>
                        
                        <div class="token-info">
                            <strong>Link ini berlaku selama 1 jam.</strong><br>
                            Setelah 1 jam, Anda perlu request reset password lagi.
                        </div>
                        
                        <div class="warning">
                            <strong>Penting:</strong> Jika Anda tidak melakukan request ini, abaikan email ini dan password Anda tetap aman.
                        </div>
                        
                        <div class="footer">
                            <p>Email ini dikirim secara otomatis oleh sistem.<br>
                            Mohon tidak membalas email ini.</p>
                            <p style="margin-top: 15px; color: #a0aec0; font-size: 12px;">
                                © 2025 PT. Pandawa Digital Indonesia. All rights reserved.
                            </p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            admin.getNamaLengkap(),
            resetLink,
            resetLink
        );
        
        try {
            emailService.sendEmail(admin.getEmail(), subject, htmlContent);
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengirim email reset password: " + e.getMessage());
        }
    }
    
    /**
     * Map to DTO - UPDATED
     */
    private AdminDTO mapToDTO(Admin admin) {
        AdminDTO dto = new AdminDTO();
        dto.setIdAdmin(admin.getIdAdmin());
        dto.setUsername(admin.getUsername());
        dto.setNamaLengkap(admin.getNamaLengkap());
        dto.setEmail(admin.getEmail());
        dto.setRole(admin.getRole());
        dto.setIsActive(admin.getIsActive());
        dto.setIsFirstLogin(admin.getIsFirstLogin());
        dto.setFotoProfil(admin.getFotoProfil());
        dto.setLastLogin(admin.getLastLogin());
        dto.setCreatedAt(admin.getCreatedAt());
        return dto;
    }
}