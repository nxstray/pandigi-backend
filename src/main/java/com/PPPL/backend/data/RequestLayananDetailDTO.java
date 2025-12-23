package com.PPPL.backend.data;

import com.PPPL.backend.model.StatusRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestLayananDetailDTO {

    // Request
    private Integer idRequest;
    private Date tglRequest;
    private StatusRequest status;
    private Date tglVerifikasi;
    private String keteranganPenolakan;

    // Klien
    private Integer idKlien;
    private String namaKlien;
    private String emailKlien;
    private String noTelpKlien;
    private String perusahaan;

    // Layanan
    private Integer idLayanan;
    private String namaLayanan;
    private String kategoriLayanan;

    // Form detail
    private String pesan;
    private String anggaran;
    private String waktuImplementasi;

    // AI
    private Boolean aiAnalyzed;
    private String skorPrioritas;
    private String kategoriLead;
    private String alasanSkor;
}