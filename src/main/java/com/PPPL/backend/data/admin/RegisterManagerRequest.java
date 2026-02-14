package com.PPPL.backend.data.admin;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterManagerRequest {
    
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
    
    @Pattern(
        regexp = "^(\\+62|62|0)[0-9]{9,12}$",
        message = "Nomor telepon harus format Indonesia (contoh: 08123456789 atau +628123456789)"
    )
    private String noTelp;
    
    @NotBlank(message = "Divisi wajib diisi")
    @Size(min = 2, max = 100, message = "Divisi harus antara 2-100 karakter")
    private String divisi;
}