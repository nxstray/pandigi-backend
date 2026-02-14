package com.PPPL.backend.service.rekap;

import com.PPPL.backend.data.rekap.RekapDTO;
import com.PPPL.backend.handler.ResourceNotFoundException;
import com.PPPL.backend.model.admin.Klien;
import com.PPPL.backend.model.enums.StatusRekap;
import com.PPPL.backend.model.layanan.Layanan;
import com.PPPL.backend.model.rekap.Rekap;
import com.PPPL.backend.repository.client.KlienRepository;
import com.PPPL.backend.repository.layanan.LayananRepository;
import com.PPPL.backend.repository.rekap.RekapRepository;
import com.PPPL.backend.security.AuthUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RekapService {

    private static final Logger log = LoggerFactory.getLogger(RekapService.class);

    @Autowired
    private RekapRepository rekapRepository;

    @Autowired
    private KlienRepository klienRepository;

    @Autowired
    private LayananRepository layananRepository;

    public List<RekapDTO> getAllRekap() {
        return rekapRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .sorted(Comparator.comparing(RekapDTO::getTglMeeting).reversed())
            .collect(Collectors.toList());
    }

    public RekapDTO getRekapById(Integer id) {
        Rekap rekap = rekapRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Rekap dengan ID " + id + " tidak ditemukan"));
        return convertToDTO(rekap);
    }

    public List<RekapDTO> searchRekap(String keyword, StatusRekap status) {
        List<Rekap> rekap = rekapRepository.findAll();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            rekap = rekap.stream()
                .filter(r -> {
                    boolean matchKlien = r.getKlien().getNamaKlien().toLowerCase().contains(kw);
                    boolean matchManager = false;
                    if (r.getNamaManagerManual() != null && !r.getNamaManagerManual().isEmpty()) {
                        matchManager = r.getNamaManagerManual().toLowerCase().contains(kw);
                    } else if (r.getManager() != null) {
                        matchManager = r.getManager().getNamaManager().toLowerCase().contains(kw);
                    }
                    return matchKlien || matchManager;
                })
                .collect(Collectors.toList());
        }

        if (status != null) {
            rekap = rekap.stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
        }

        return rekap.stream()
            .map(this::convertToDTO)
            .sorted(Comparator.comparing(RekapDTO::getTglMeeting).reversed())
            .collect(Collectors.toList());
    }

    public List<RekapDTO> getRekapByKlien(Integer idKlien) {
        return rekapRepository.findAll()
            .stream()
            .filter(r -> r.getKlien().getIdKlien().equals(idKlien))
            .map(this::convertToDTO)
            .sorted(Comparator.comparing(RekapDTO::getTglMeeting).reversed())
            .collect(Collectors.toList());
    }

    public List<RekapDTO> getRekapByManager(Integer idManager) {
        return rekapRepository.findAll()
            .stream()
            .filter(r -> r.getManager() != null &&
                r.getManager().getIdManager().equals(idManager))
            .map(this::convertToDTO)
            .sorted(Comparator.comparing(RekapDTO::getTglMeeting).reversed())
            .collect(Collectors.toList());
    }

    @Transactional
    public RekapDTO createRekap(RekapDTO dto) {
        Klien klien = klienRepository.findById(dto.getIdKlien())
            .orElseThrow(() -> new ResourceNotFoundException("Klien tidak ditemukan"));

        if (dto.getNamaManagerManual() == null || dto.getNamaManagerManual().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama manager wajib diisi");
        }

        Layanan layanan = layananRepository.findById(dto.getIdLayanan())
            .orElseThrow(() -> new ResourceNotFoundException("Layanan tidak ditemukan"));

        Rekap rekap = new Rekap();
        rekap.setKlien(klien);
        rekap.setManager(null);
        rekap.setNamaManagerManual(dto.getNamaManagerManual().trim());
        rekap.setLayanan(layanan);
        rekap.setTglMeeting(dto.getTglMeeting());
        rekap.setHasil(dto.getHasil());
        rekap.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusRekap.MASIH_JALAN);
        rekap.setCatatan(dto.getCatatan());

        Rekap saved = rekapRepository.save(rekap);
        log.info("Rekap created: id={}, klien={}", saved.getIdMeeting(), klien.getNamaKlien());

        return convertToDTO(saved);
    }

    /**
     * Update rekap with validation of ownership
     */
    @Transactional
    public RekapDTO updateRekapDTO(Integer idMeeting, RekapDTO dto, AuthUser auth) {
        Rekap updated = updateRekap(idMeeting, dto, auth);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteRekap(Integer id) {
        Rekap rekap = rekapRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Rekap dengan ID " + id + " tidak ditemukan"));
        rekapRepository.delete(rekap);
        log.info("Rekap deleted: id={}", id);
    }

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
     * Update rekap with validation of ownership
     */
    @Transactional
    public Rekap updateRekap(Integer idMeeting, RekapDTO dto, AuthUser auth) {
        Rekap rekap = rekapRepository.findById(idMeeting)
            .orElseThrow(() -> new ResourceNotFoundException("Rekap tidak ditemukan"));

        if ("MANAGER".equals(auth.role())) {
            if (rekap.getManager() != null &&
                !rekap.getManager().getIdManager().equals(auth.userId())) {
                throw new SecurityException("Anda tidak berhak mengubah rekap ini");
            }
        }

        if ("SUPER_ADMIN".equals(auth.role())) {
            if (dto.getIdKlien() != null) {
                Klien klien = klienRepository.findById(dto.getIdKlien())
                    .orElseThrow(() -> new ResourceNotFoundException("Klien tidak ditemukan"));
                rekap.setKlien(klien);
            }

            if (dto.getNamaManagerManual() != null &&
                !dto.getNamaManagerManual().trim().isEmpty()) {
                rekap.setNamaManagerManual(dto.getNamaManagerManual().trim());
                rekap.setManager(null);
            }

            if (dto.getIdLayanan() != null) {
                Layanan layanan = layananRepository.findById(dto.getIdLayanan())
                    .orElseThrow(() -> new ResourceNotFoundException("Layanan tidak ditemukan"));
                rekap.setLayanan(layanan);
            }
        }

        if (dto.getTglMeeting() != null) rekap.setTglMeeting(dto.getTglMeeting());
        if (dto.getHasil() != null) rekap.setHasil(dto.getHasil());
        if (dto.getStatus() != null) rekap.setStatus(dto.getStatus());
        if (dto.getCatatan() != null) rekap.setCatatan(dto.getCatatan());

        return rekapRepository.save(rekap);
    }

    /**
     * Add new note to rekap meeting's existing notes
     */
    @Transactional
    public Rekap tambahCatatan(Integer idMeeting, String catatan) {
        Rekap rekap = rekapRepository.findById(idMeeting)
            .orElseThrow(() -> new RuntimeException("Rekap meeting tidak ditemukan"));

        String catatanLama = rekap.getCatatan() != null ? rekap.getCatatan() : "";
        rekap.setCatatan(catatanLama + "\n[" + new java.util.Date() + "] " + catatan);

        return rekapRepository.save(rekap);
    }

    // Private helper methods
    private RekapDTO convertToDTO(Rekap rekap) {
        RekapDTO dto = new RekapDTO();
        dto.setIdMeeting(rekap.getIdMeeting());
        dto.setIdKlien(rekap.getKlien().getIdKlien());
        dto.setNamaKlien(rekap.getKlien().getNamaKlien());

        if (rekap.getNamaManagerManual() != null && !rekap.getNamaManagerManual().isEmpty()) {
            dto.setNamaManager(rekap.getNamaManagerManual());
            dto.setNamaManagerManual(rekap.getNamaManagerManual());
        } else if (rekap.getManager() != null) {
            dto.setNamaManager(rekap.getManager().getNamaManager());
            dto.setNamaManagerManual(rekap.getManager().getNamaManager());
        } else {
            dto.setNamaManager("-");
            dto.setNamaManagerManual("-");
        }

        dto.setIdLayanan(rekap.getLayanan().getIdLayanan());
        dto.setNamaLayanan(rekap.getLayanan().getNamaLayanan());
        dto.setTglMeeting(rekap.getTglMeeting());
        dto.setHasil(rekap.getHasil());
        dto.setStatus(rekap.getStatus());
        dto.setCatatan(rekap.getCatatan());

        return dto;
    }
}