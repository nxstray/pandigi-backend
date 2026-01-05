package com.PPPL.backend.data;

import com.PPPL.backend.model.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Integer idAdmin;
    private String username;
    private String namaLengkap;
    private String email;
    private AdminRole role;
    private String fotoProfil;
    private Boolean isFirstLogin;
    
    public LoginResponse(String token, Integer idAdmin, String username, String namaLengkap, 
                        String email, AdminRole role, String fotoProfil, Boolean isFirstLogin) {
        this.token = token;
        this.idAdmin = idAdmin;
        this.username = username;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.role = role;
        this.fotoProfil = fotoProfil;
        this.isFirstLogin = isFirstLogin;
    }
}