package com.PPPL.backend.data.admin;

import com.PPPL.backend.model.enums.AdminRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAdminRequest {
    
    @NotBlank(message = "Username wajib diisi")
    @Size(min = 4, max = 50, message = "Username harus antara 4-50 karakter")
    @Pattern(
        regexp = "^[a-zA-Z0-9_-]+$",
        message = "Username hanya boleh mengandung huruf, angka, underscore (_), dan strip (-)"
    )
    private String username;
    
    @NotBlank(message = "Password wajib diisi")
    @Size(min = 8, max = 100, message = "Password minimal 8 karakter")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password harus mengandung minimal: 1 huruf besar, 1 huruf kecil, 1 angka, dan 1 karakter spesial (@$!%*?&)"
    )
    private String password;
    
    @NotBlank(message = "Nama lengkap wajib diisi")
    @Size(min = 3, max = 100, message = "Nama harus antara 3-100 karakter")
    @Pattern(
        regexp = "^[a-zA-Z\\s.'-]+$",
        message = "Nama hanya boleh mengandung huruf, spasi, titik, apostrof, dan strip"
    )
    private String namaLengkap;
    
    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    @Size(max = 100, message = "Email maksimal 100 karakter")
    private String email;
    
    @NotNull(message = "Role wajib dipilih")
    private AdminRole role;
}