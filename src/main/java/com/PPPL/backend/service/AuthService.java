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