package com.PPPL.backend.data.rekap;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

import com.PPPL.backend.model.enums.StatusRekap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RekapDTO {
    private Integer idMeeting;

    @NotNull(message = "Klien wajib dipilih")
    @Positive(message = "ID klien tidak valid")
    private Integer idKlien;

    private String namaKlien;
    private String namaManager;
    private String namaManagerManual;

    @NotNull(message = "Layanan wajib dipilih")
    @Positive(message = "ID layanan tidak valid")
    private Integer idLayanan;

    private String namaLayanan;

    @NotNull(message = "Tanggal meeting wajib diisi")
    @PastOrPresent(message = "Tanggal meeting tidak boleh di masa depan")
    private Date tglMeeting;

    @NotBlank(message = "Hasil meeting wajib diisi")
    @Size(min = 10, max = 2000, message = "Hasil meeting harus antara 10-2000 karakter")
    private String hasil;

    @NotNull(message = "Status wajib dipilih")
    private StatusRekap status;

    @Size(max = 1000, message = "Catatan maksimal 1000 karakter")
    private String catatan;
}