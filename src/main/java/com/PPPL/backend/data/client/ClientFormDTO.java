package com.PPPL.backend.data.client;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientFormDTO {
    // Personal Info
    @NotBlank(message = "Nama depan wajib diisi")
    @Size(min = 2, max = 50, message = "Nama depan harus antara 2-50 karakter")
    @Pattern(regexp = "^[a-zA-Z\\s.'-]+$", message = "Nama depan hanya boleh mengandung huruf")
    private String firstName;

    @NotBlank(message = "Nama belakang wajib diisi")
    @Size(min = 2, max = 50, message = "Nama belakang harus antara 2-50 karakter")
    @Pattern(regexp = "^[a-zA-Z\\s.'-]+$", message = "Nama belakang hanya boleh mengandung huruf")
    private String lastName;

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    @Size(max = 100, message = "Email maksimal 100 karakter")
    private String email;

    @NotBlank(message = "Nomor telepon wajib diisi")
    @Pattern(
        regexp = "^(\\+62|62|0)[0-9]{9,12}$",
        message = "Nomor telepon harus format Indonesia (contoh: 08123456789)"
    )
    private String phoneNumber;

    // Service Request
    @NotNull(message = "Layanan wajib dipilih")
    @Positive(message = "ID layanan tidak valid")
    private Integer idLayanan;

    @NotBlank(message = "Pesan wajib diisi")
    @Size(min = 10, max = 1000, message = "Pesan harus antara 10-1000 karakter")
    private String message;     // Mapping ke RequestLayanan.pesan

    // Additional Lead Scoring Data
    @Size(max = 100, message = "Nama perusahaan maksimal 100 karakter")
    private String perusahaan;

    @Size(max = 100, message = "Anggaran maksimal 100 karakter")
    private String anggaran;

    @Size(max = 100, message = "Waktu implementasi maksimal 100 karakter")
    private String waktuImplementasi;
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}