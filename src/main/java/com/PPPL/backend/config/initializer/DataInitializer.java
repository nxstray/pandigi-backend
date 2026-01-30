package com.PPPL.backend.config.initializer;

import com.PPPL.backend.model.admin.Admin;
import com.PPPL.backend.model.enums.AdminRole;
import com.PPPL.backend.repository.admin.AdminRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.superadmin.username:superadmin}")
    private String superAdminUsername;
    
    @Value("${app.superadmin.password:}")
    private String superAdminPassword;
    
    @Value("${app.superadmin.email:admin@pandawa.com}")
    private String superAdminEmail;
    
    @Value("${app.superadmin.name:Super Administrator}")
    private String superAdminName;
    
    @Override
    public void run(String... args) throws Exception {
        // Only create if not exists
        if (!adminRepository.existsByUsername(superAdminUsername)) {
            
            // Validation: Password must be provided in production
            if (superAdminPassword == null || superAdminPassword.trim().isEmpty()) {
                System.err.println("========================================");
                System.err.println("WARNING: SUPER_ADMIN password not set!");
                System.err.println("Set environment variable: APP_SUPERADMIN_PASSWORD");
                System.err.println("========================================");
                return;
            }
            
            Admin superAdmin = new Admin();
            superAdmin.setUsername(superAdminUsername);
            superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
            superAdmin.setNamaLengkap(superAdminName);
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setRole(AdminRole.SUPER_ADMIN);
            superAdmin.setIsActive(true);
            superAdmin.setIsFirstLogin(false);
            
            adminRepository.save(superAdmin);
            
            System.out.println("========================================");
            System.out.println("SUPER_ADMIN Created Successfully!");
            System.out.println("Username: " + superAdminUsername);
            System.out.println("Email: " + superAdminEmail);
            System.out.println("========================================");
        }
    }
}