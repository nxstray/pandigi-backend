package com.PPPL.backend.service;

import com.PPPL.backend.data.RekapDTO;
import com.PPPL.backend.model.*;
import com.PPPL.backend.repository.*;
import com.PPPL.backend.security.AuthUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RekapService {
    
    @Autowired
    private RekapRepository rekapRepository;

    @Autowired
    private KlienRepository klienRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private LayananRepository layananRepository;
    
    /**
     * Update status rekap meeting
     */
    @Transactional
    public Rekap updateStatusRekap(Integer idMeeting, StatusRekap status, String catatanTambahan) {
        Rekap rekap = rekapRepository.findById(idMeeting)
            .orElseThrow(() -> new RuntimeException("Rekap meeting tidak ditemukan"));
        
        rekap.setStatus(status);
        
        if (catatanTambahan != null && !catatanTambahan.isEmpty()) {
            String catatanLama = rekap.getCatatan() != null ? rekap.getCatatan() : "";
            rekap.setCatatan(catatanLama + "\n" + catatanTambahan);
        }
        
        return rekapRepository.save(rekap);
    }

    /**
     * Update rekap meeting dengan validasi kepemilikan
     */
    @Transactional
    public Rekap updateRekap(
            Integer idMeeting,
            RekapDTO dto,
            AuthUser auth
    ) {
        Rekap rekap = rekapRepository.findById(idMeeting)
                .orElseThrow(() -> new RuntimeException("Rekap tidak ditemukan"));

        // ================= VALIDASI KEPEMILIKAN =================
        if ("MANAGER".equals(auth.role())) {
            if (!rekap.getManager().getIdManager().equals(auth.userId())) {
                throw new SecurityException("Anda tidak berhak mengubah rekap ini");
            }
        }

        // ================= UPDATE RELASI (HANYA SUPER_ADMIN) =================
        if ("SUPER_ADMIN".equals(auth.role())) {

            if (dto.getIdKlien() != null) {
                Klien klien = klienRepository.findById(dto.getIdKlien())
                        .orElseThrow(() -> new RuntimeException("Klien tidak ditemukan"));
                rekap.setKlien(klien);
            }

            if (dto.getIdManager() != null) {
                Manager manager = managerRepository.findById(dto.getIdManager())
                        .orElseThrow(() -> new RuntimeException("Manager tidak ditemukan"));
                rekap.setManager(manager);
            }

            if (dto.getIdLayanan() != null) {
                Layanan layanan = layananRepository.findById(dto.getIdLayanan())
                        .orElseThrow(() -> new RuntimeException("Layanan tidak ditemukan"));
                rekap.setLayanan(layanan);
            }
        }

        // ================= UPDATE FIELD UMUM =================
        if (dto.getTglMeeting() != null) {
            rekap.setTglMeeting(dto.getTglMeeting());
        }

        if (dto.getHasil() != null) {
            rekap.setHasil(dto.getHasil());
        }

        if (dto.getStatus() != null) {
            rekap.setStatus(dto.getStatus());
        }

        if (dto.getCatatan() != null) {
            rekap.setCatatan(dto.getCatatan());
        }

        return rekapRepository.save(rekap);
    }
    
    /**
     * Menambahkan catatan baru ke rekap
     */
    @Transactional
    public Rekap tambahCatatan(Integer idMeeting, String catatan) {
        Rekap rekap = rekapRepository.findById(idMeeting)
            .orElseThrow(() -> new RuntimeException("Rekap meeting tidak ditemukan"));
        
        String catatanLama = rekap.getCatatan() != null ? rekap.getCatatan() : "";
        rekap.setCatatan(catatanLama + "\n[" + new java.util.Date() + "] " + catatan);
        
        return rekapRepository.save(rekap);
    }
}