package com.PPPL.backend.data.layanan;

import com.PPPL.backend.model.enums.KategoriLayanan;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LayananDTO {
    private Integer idLayanan;

    @NotBlank(message = "Nama layanan wajib diisi")
    @Size(min = 3, max = 100, message = "Nama layanan harus antara 3-100 karakter")
    private String namaLayanan;

    @NotNull(message = "Kategori wajib dipilih")
    private KategoriLayanan kategori;

    @Size(max = 500, message = "Catatan maksimal 500 karakter")
    private String catatan;
}