package com.PPPL.backend.util;

import com.PPPL.backend.data.admin.KaryawanDTO;
import com.PPPL.backend.data.admin.ManagerDTO;
import com.PPPL.backend.data.client.KlienDTO;
import com.PPPL.backend.data.layanan.LayananDTO;
import com.PPPL.backend.data.layanan.RequestLayananDTO;
import com.PPPL.backend.data.rekap.RekapDTO;
import com.PPPL.backend.model.admin.Karyawan;
import com.PPPL.backend.model.admin.Klien;
import com.PPPL.backend.model.admin.Manager;
import com.PPPL.backend.model.layanan.Layanan;
import com.PPPL.backend.model.layanan.RequestLayanan;
import com.PPPL.backend.model.rekap.Rekap;

import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
    
    public KaryawanDTO toDTO(Karyawan karyawan) {
        KaryawanDTO dto = new KaryawanDTO();
        dto.setIdKaryawan(karyawan.getIdKaryawan());
        dto.setNamaKaryawan(karyawan.getNamaKaryawan());
        dto.setEmailKaryawan(karyawan.getEmailKaryawan());
        dto.setNoTelp(karyawan.getNoTelp());
        dto.setJabatanPosisi(karyawan.getJabatanPosisi());
        if (karyawan.getManager() != null) {
            dto.setIdManager(karyawan.getManager().getIdManager());
            dto.setNamaManager(karyawan.getManager().getNamaManager());
        }
        return dto;
    }
    
    public ManagerDTO toDTO(Manager manager) {
        ManagerDTO dto = new ManagerDTO();
        dto.setIdManager(manager.getIdManager());
        dto.setNamaManager(manager.getNamaManager());
        dto.setEmailManager(manager.getEmailManager());
        dto.setNoTelp(manager.getNoTelp());
        dto.setDivisi(manager.getDivisi());
        dto.setTglMulai(manager.getTglMulai());
        return dto;
    }
    
    public KlienDTO toDTO(Klien klien) {
        KlienDTO dto = new KlienDTO();
        dto.setIdKlien(klien.getIdKlien());
        dto.setNamaKlien(klien.getNamaKlien());
        dto.setEmailKlien(klien.getEmailKlien());
        dto.setNoTelp(klien.getNoTelp());
        dto.setStatus(klien.getStatus());
        dto.setTglRequest(klien.getTglRequest());
        return dto;
    }
    
    public LayananDTO toDTO(Layanan layanan) {
        LayananDTO dto = new LayananDTO();
        dto.setIdLayanan(layanan.getIdLayanan());
        dto.setNamaLayanan(layanan.getNamaLayanan());
        dto.setKategori(layanan.getKategori());
        dto.setCatatan(layanan.getCatatan());
        return dto;
    }
    
    public RequestLayananDTO toDTO(RequestLayanan request) {
        RequestLayananDTO dto = new RequestLayananDTO();
        dto.setIdRequest(request.getIdRequest());
        dto.setTglRequest(request.getTglRequest());
        dto.setStatus(request.getStatus());
        dto.setTglVerifikasi(request.getTglVerifikasi());
        dto.setKeteranganPenolakan(request.getKeteranganPenolakan());
        
        if (request.getLayanan() != null) {
            dto.setIdLayanan(request.getLayanan().getIdLayanan());
            dto.setNamaLayanan(request.getLayanan().getNamaLayanan());
        }
        
        if (request.getKlien() != null) {
            dto.setIdKlien(request.getKlien().getIdKlien());
            dto.setNamaKlien(request.getKlien().getNamaKlien());
        }
        
        return dto;
    }
    
    public RekapDTO toDTO(Rekap rekap) {
        RekapDTO dto = new RekapDTO();
        dto.setIdMeeting(rekap.getIdMeeting());
        dto.setTglMeeting(rekap.getTglMeeting());
        dto.setHasil(rekap.getHasil());
        dto.setStatus(rekap.getStatus());
        dto.setCatatan(rekap.getCatatan());
        
        if (rekap.getKlien() != null) {
            dto.setIdKlien(rekap.getKlien().getIdKlien());
            dto.setNamaKlien(rekap.getKlien().getNamaKlien());
        }
        
        if (rekap.getManager() != null) {
            dto.setNamaManager(rekap.getManager().getNamaManager());
        }
        
        if (rekap.getLayanan() != null) {
            dto.setIdLayanan(rekap.getLayanan().getIdLayanan());
            dto.setNamaLayanan(rekap.getLayanan().getNamaLayanan());
        }
        
        return dto;
    }
}
