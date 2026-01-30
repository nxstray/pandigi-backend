package com.PPPL.backend.model.admin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

import com.PPPL.backend.model.enums.AdminRole;

@Entity
@Table(name = "admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Integer idAdmin;
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "nama_lengkap", nullable = false, length = 100)
    private String namaLengkap;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AdminRole role = AdminRole.MANAGER;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_first_login")
    private Boolean isFirstLogin = true;
    
    @Column(name = "foto_profil", columnDefinition = "TEXT")
    private String fotoProfil;

    @Column(name = "reset_token", length = 255)
    private String resetToken;
    
    @Column(name = "reset_token_expiry")
    @Temporal(TemporalType.TIMESTAMP)
    private Date resetTokenExpiry;
    
    @Column(name = "last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}