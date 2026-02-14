package com.PPPL.backend.data.admin;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KaryawanDTO {
    
    private Integer idKaryawan;
    
    @NotBlank(message = "Nama karyawan wajib diisi")
    @Size(min = 3, max = 100, message = "Nama harus antara 3-100 karakter")
    @Pattern(
        regexp = "^[a-zA-Z\\s.'-]+$", 
        message = "Nama hanya boleh mengandung huruf, spasi, titik, apostrof, dan strip"
    )
    private String namaKaryawan;
    
    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    @Size(max = 100, message = "Email maksimal 100 karakter")
    private String emailKaryawan;
    
    @Pattern(
        regexp = "^(\\+62|62|0)[0-9]{9,12}$",
        message = "Nomor telepon harus format Indonesia (contoh: 08123456789 atau +628123456789)"
    )
    private String noTelp;
    
    @NotBlank(message = "Jabatan/posisi wajib diisi")
    @Size(min = 3, max = 100, message = "Jabatan harus antara 3-100 karakter")
    private String jabatanPosisi;
    
    @NotNull(message = "Manager wajib dipilih")
    @Positive(message = "ID Manager tidak valid")
    private Integer idManager;
    
    // Display only - no need validation
    private String namaManager;
    
    @Size(max = 500, message = "Path foto profil maksimal 500 karakter")
    private String fotoProfil;
}