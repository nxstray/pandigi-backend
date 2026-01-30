package com.PPPL.backend.data.layanan;

import com.PPPL.backend.model.enums.KategoriLayanan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LayananDTO {
    private Integer idLayanan;
    private String namaLayanan;
    private KategoriLayanan kategori;
    private String catatan;
}