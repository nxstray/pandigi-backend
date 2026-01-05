package com.PPPL.backend.data;

import com.PPPL.backend.model.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {
    private Integer idAdmin;
    private String username;
    private String namaLengkap;
    private String email;
    private AdminRole role;
    private Boolean isActive;
    private Boolean isFirstLogin;
    private String fotoProfil;
    private Date lastLogin;
    private Date createdAt;
}