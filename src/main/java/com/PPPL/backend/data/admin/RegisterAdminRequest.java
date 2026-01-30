package com.PPPL.backend.data.admin;

import com.PPPL.backend.model.enums.AdminRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAdminRequest {
    private String username;
    private String password;
    private String namaLengkap;
    private String email;
    private AdminRole role;
}
