package com.PPPL.backend.service;

import com.PPPL.backend.data.AdminDTO;
import com.PPPL.backend.data.RegisterManagerRequest;
import com.PPPL.backend.model.Admin;
import com.PPPL.backend.model.AdminRole;
import com.PPPL.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;
    
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Register manager baru dengan auto-generated password dan email notification
     */
    @Transactional
    public AdminDTO registerManager(RegisterManagerRequest request) {
        // Validate email
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email sudah digunakan");
        }
        
        // Generate username dari email (bagian sebelum @)
        String username = generateUsernameFromEmail(request.getEmail());
        
        // Pastikan username unik
        String finalUsername = username;
        int counter = 1;
        while (adminRepository.existsByUsername(finalUsername)) {
            finalUsername = username + counter;
            counter++;
        }
        
        // Generate random password
        String temporaryPassword = generateRandomPassword();
        
        // Create new manager
        Admin manager = new Admin();
        manager.setUsername(finalUsername);
        manager.setPassword(passwordEncoder.encode(temporaryPassword));
        manager.setNamaLengkap(request.getNamaLengkap());
        manager.setEmail(request.getEmail());
        manager.setRole(AdminRole.MANAGER);
        manager.setIsActive(true);
        manager.setIsFirstLogin(true);
        
        Admin saved = adminRepository.save(manager);
        
        // Send email with credentials
        sendWelcomeEmail(saved, temporaryPassword);
        
        return mapToDTO(saved);
    }
    
    /**
     * Generate username dari email
     */
    private String generateUsernameFromEmail(String email) {
        return email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9]", "");
    }
    
    /**
     * Generate random password dengan requirement:
     * - Minimal 8 karakter
     * - Minimal 1 uppercase
     * - Minimal 1 lowercase  
     * - Minimal 1 digit
     * - Minimal 1 special character
     */
    private String generateRandomPassword() {
        String SAFE_SPECIAL = "!@#$%^&*()_+-=[]{}|:<>?";
        String ALL_SAFE_CHARS = UPPERCASE + LOWERCASE + DIGITS + SAFE_SPECIAL;
        
        StringBuilder password = new StringBuilder(12);
        
        // Ensure at least one of each required character type
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SAFE_SPECIAL.charAt(random.nextInt(SAFE_SPECIAL.length())));
        
        // Fill remaining 8 characters randomly (total 12)
        for (int i = 4; i < 12; i++) {
            password.append(ALL_SAFE_CHARS.charAt(random.nextInt(ALL_SAFE_CHARS.length())));
        }
        
        // Shuffle the password
        return shuffleString(password.toString());
    }

    /**
     * Shuffle string untuk randomize password
     */
    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
    
    /**
     * Send welcome email dengan credentials
     */
    private void sendWelcomeEmail(Admin manager, String temporaryPassword) {
        String subject = "Akun Manager Anda - PT. Pandawa Digital Mandiri";
        
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
                    .credentials {
                        background: #f7fafc;
                        border-left: 4px solid #0C2B40;
                        padding: 20px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .credential-item {
                        margin: 10px 0;
                    }
                    .credential-label {
                        font-weight: bold;
                        color: #667eea;
                        display: inline-block;
                        width: 120px;
                    }
                    .credential-value {
                        font-family: 'Courier New', monospace;
                        background: white;
                        padding: 5px 10px;
                        border-radius: 4px;
                        border: 1px solid #e2e8f0;
                    }
                    .warning {
                        background: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .warning-icon {
                        color: #ffc107;
                        font-size: 20px;
                        margin-right: 10px;
                    }
                    .btn {
                        display: inline-block;
                        background: #0C2B40;
                        color: #ffffff !important;
                        padding: 12px 30px;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 20px 0;
                        font-weight: bold;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #e2e8f0;
                        color: #718096;
                        font-size: 14px;
                    }
                    .steps {
                        background: #f7fafc;
                        padding: 20px;
                        border-radius: 4px;
                        margin: 20px 0;
                    }
                    .step {
                        margin: 10px 0;
                        padding-left: 30px;
                        position: relative;
                    }
                    .step:before {
                        content: "✓";
                        position: absolute;
                        left: 0;
                        color: #0C2B40;
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="content">
                        <div class="logo">
                            <h1>PT. Pandawa Digital Mandiri</h1>
                        </div>
                        
                        <h2 style="color: #2d3748;">Selamat Datang, %s!</h2>
                        
                        <p>Akun Manager Anda telah berhasil dibuat. Berikut adalah kredensial login Anda:</p>
                        
                        <div class="credentials">
                            <div class="credential-item">
                                <span class="credential-label">Username:</span>
                                <span class="credential-value">%s</span>
                            </div>
                            <div class="credential-item">
                                <span class="credential-label">Password:</span>
                                <span class="credential-value">%s</span>
                            </div>
                            <div class="credential-item">
                                <span class="credential-label">Email:</span>
                                <span class="credential-value">%s</span>
                            </div>
                        </div>
                        
                        <div class="warning">
                            <strong>WARNING:</strong> Untuk keamanan akun Anda, harap segera mengubah password setelah login pertama kali.
                        </div>
                        
                        <center>
                            <a href="%s/login" class="btn">Login Sekarang</a>
                        </center>
                        
                        <div class="footer">
                            <p>Email ini dikirim secara otomatis oleh sistem.<br>
                            Jika Anda memiliki pertanyaan, silakan hubungi administrator.</p>
                            <p style="margin-top: 15px; color: #a0aec0; font-size: 12px;">
                                © 2025 PT. Pandawa Digital Mandiri. All rights reserved.
                            </p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            manager.getNamaLengkap(),
            manager.getUsername(),
            temporaryPassword,
            manager.getEmail(),
            frontendUrl
        );
        
        try {
            emailService.sendEmail(manager.getEmail(), subject, htmlContent);
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengirim email: " + e.getMessage());
        }
    }
    
    /**
     * Map to DTO
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