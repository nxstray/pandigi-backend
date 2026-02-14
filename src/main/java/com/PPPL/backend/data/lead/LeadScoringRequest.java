package com.PPPL.backend.data.lead;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadScoringRequest {
    @NotNull(message = "ID request wajib diisi")
    @Positive(message = "ID request tidak valid")
    private Integer idRequest;
    
    @NotBlank(message = "Nama klien wajib diisi")
    private String namaKlien;

    private String perusahaan;
    @NotBlank(message = "Layanan wajib diisi")
    private String layanan;

    private String topic;

    @NotBlank(message = "Pesan wajib diisi")
    @Size(min = 10, max = 2000, message = "Pesan harus antara 10-2000 karakter")
    private String pesan;

    private String anggaran;
    private String waktuImplementasi;
    private String emailKlien;
    private String noTelp;
}