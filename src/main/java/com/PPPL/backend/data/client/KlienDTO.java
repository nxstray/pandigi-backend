package com.PPPL.backend.data.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

import com.PPPL.backend.model.enums.StatusKlien;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KlienDTO {
    private Integer idKlien;
    private String namaKlien;
    private String emailKlien;
    private String noTelp;
    private StatusKlien status;
    private Date tglRequest;
    private String lastApprovedBy;
}