package com.PPPL.backend.service.admin;

import com.PPPL.backend.data.admin.KaryawanDTO;
import com.PPPL.backend.handler.ResourceNotFoundException;
import com.PPPL.backend.model.admin.Karyawan;
import com.PPPL.backend.model.admin.Manager;
import com.PPPL.backend.repository.admin.KaryawanRepository;
import com.PPPL.backend.repository.admin.ManagerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KaryawanService {

    private static final Logger log = LoggerFactory.getLogger(KaryawanService.class);

    @Autowired
    private KaryawanRepository karyawanRepository;

    @Autowired
    private ManagerRepository managerRepository;

    /**
     * Get all karyawan
     */
    public List<KaryawanDTO> getAllKaryawan() {
        return karyawanRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get karyawan by ID
     */
    public KaryawanDTO getKaryawanById(Integer id) {
        Karyawan karyawan = karyawanRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Karyawan dengan ID " + id + " tidak ditemukan"
            ));

        return convertToDTO(karyawan);
    }

    /**
     * Create new karyawan
     */
    @Transactional
    public KaryawanDTO createKaryawan(KaryawanDTO dto) {
        // Check duplicate email
        if (karyawanRepository.existsByEmailKaryawan(dto.getEmailKaryawan())) {
            throw new IllegalArgumentException(
                "Email '" + dto.getEmailKaryawan() + "' sudah terdaftar"
            );
        }

        // Validate if manager exists
        Manager manager = managerRepository.findById(dto.getIdManager())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Manager dengan ID " + dto.getIdManager() + " tidak ditemukan"
            ));

        // Make entity
        Karyawan karyawan = new Karyawan();
        karyawan.setNamaKaryawan(dto.getNamaKaryawan().trim());
        karyawan.setEmailKaryawan(dto.getEmailKaryawan().trim().toLowerCase());
        karyawan.setNoTelp(dto.getNoTelp());
        karyawan.setJabatanPosisi(dto.getJabatanPosisi().trim());
        karyawan.setManager(manager);
        karyawan.setFotoProfil(dto.getFotoProfil());

        Karyawan saved = karyawanRepository.save(karyawan);

        log.info("Karyawan created: id={}, nama={}, manager={}",
            saved.getIdKaryawan(), saved.getNamaKaryawan(), manager.getNamaManager());

        return convertToDTO(saved);
    }

    /**
     * Update karyawan
     */
    @Transactional
    public KaryawanDTO updateKaryawan(Integer id, KaryawanDTO dto) {
        // Search karyawan wants to be updated
        Karyawan karyawan = karyawanRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Karyawan dengan ID " + id + " tidak ditemukan"
            ));

        // Check duplicate email if only changed
        String emailBaru = dto.getEmailKaryawan().trim().toLowerCase();
        String emailLama = karyawan.getEmailKaryawan();

        if (!emailLama.equals(emailBaru) &&
            karyawanRepository.existsByEmailKaryawan(emailBaru)) {
            throw new IllegalArgumentException(
                "Email '" + emailBaru + "' sudah digunakan karyawan lain"
            );
        }

        // Validate if manager exists
        Manager manager = managerRepository.findById(dto.getIdManager())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Manager dengan ID " + dto.getIdManager() + " tidak ditemukan"
            ));

        // Update field
        karyawan.setNamaKaryawan(dto.getNamaKaryawan().trim());
        karyawan.setEmailKaryawan(emailBaru);
        karyawan.setNoTelp(dto.getNoTelp());
        karyawan.setJabatanPosisi(dto.getJabatanPosisi().trim());
        karyawan.setManager(manager);

        // Update foto only if provided
        if (dto.getFotoProfil() != null && !dto.getFotoProfil().isBlank()) {
            karyawan.setFotoProfil(dto.getFotoProfil());
        }

        Karyawan updated = karyawanRepository.save(karyawan);

        log.info("Karyawan updated: id={}, nama={}, manager={}",
            updated.getIdKaryawan(), updated.getNamaKaryawan(), manager.getNamaManager());

        return convertToDTO(updated);
    }

    /**
     * Delete karyawan
     */
    @Transactional
    public void deleteKaryawan(Integer id) {
        if (!karyawanRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Karyawan dengan ID " + id + " tidak ditemukan"
            );
        }

        karyawanRepository.deleteById(id);
        log.info("Karyawan deleted: id={}", id);
    }

    /**
     * Search karyawan by keyword dan/atau manager
     */
    public List<KaryawanDTO> searchKaryawan(String keyword, Integer idManager) {
        // Validasi idManager jika diberikan
        if (idManager != null && !managerRepository.existsById(idManager)) {
            throw new ResourceNotFoundException(
                "Manager dengan ID " + idManager + " tidak ditemukan"
            );
        }

        List<Karyawan> karyawan = karyawanRepository.findAll();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            karyawan = karyawan.stream()
                .filter(k ->
                    k.getNamaKaryawan().toLowerCase().contains(kw) ||
                    k.getEmailKaryawan().toLowerCase().contains(kw) ||
                    k.getJabatanPosisi().toLowerCase().contains(kw)
                )
                .collect(Collectors.toList());
        }

        if (idManager != null) {
            karyawan = karyawan.stream()
                .filter(k -> k.getManager().getIdManager().equals(idManager))
                .collect(Collectors.toList());
        }

        return karyawan.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get karyawan by manager
     */
    public List<KaryawanDTO> getKaryawanByManager(Integer idManager) {
        if (!managerRepository.existsById(idManager)) {
            throw new ResourceNotFoundException(
                "Manager dengan ID " + idManager + " tidak ditemukan"
            );
        }

        return karyawanRepository.findAll()
            .stream()
            .filter(k -> k.getManager().getIdManager().equals(idManager))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convert Karyawan entity to DTO
     */
    private KaryawanDTO convertToDTO(Karyawan karyawan) {
        KaryawanDTO dto = new KaryawanDTO();
        dto.setIdKaryawan(karyawan.getIdKaryawan());
        dto.setNamaKaryawan(karyawan.getNamaKaryawan());
        dto.setEmailKaryawan(karyawan.getEmailKaryawan());
        dto.setNoTelp(karyawan.getNoTelp());
        dto.setJabatanPosisi(karyawan.getJabatanPosisi());
        dto.setIdManager(karyawan.getManager().getIdManager());
        dto.setNamaManager(karyawan.getManager().getNamaManager());
        dto.setFotoProfil(karyawan.getFotoProfil());
        return dto;
    }
}