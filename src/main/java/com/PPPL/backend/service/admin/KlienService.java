package com.PPPL.backend.service.admin;

import com.PPPL.backend.data.client.KlienDTO;
import com.PPPL.backend.handler.ResourceNotFoundException;
import com.PPPL.backend.model.admin.Klien;
import com.PPPL.backend.model.admin.Manager;
import com.PPPL.backend.model.enums.StatusKlien;
import com.PPPL.backend.repository.client.KlienRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KlienService {

    private static final Logger log = LoggerFactory.getLogger(KlienService.class);

    @Autowired
    private KlienRepository klienRepository;

    public List<KlienDTO> getAllKlien() {
        return klienRepository
            .findKlienYangTerverifikasi()
            .stream()
            .filter(k -> !Boolean.TRUE.equals(k.getIsDeleted()))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Method get klien by ID
     */
    public KlienDTO getKlienById(Integer id) {
        Klien klien = klienRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Klien dengan ID " + id + " tidak ditemukan"));

        if (Boolean.TRUE.equals(klien.getIsDeleted())) {
            throw new ResourceNotFoundException("Klien tidak ditemukan");
        }

        return convertToDTO(klien);
    }

    /**
     * Mehod create new klien data
     */
    @Transactional
    public KlienDTO createKlien(KlienDTO dto) {
        if (klienRepository.findByEmailKlien(dto.getEmailKlien()).isPresent()) {
            throw new IllegalArgumentException(
                "Email '" + dto.getEmailKlien() + "' sudah terdaftar");
        }

        Klien klien = new Klien();
        klien.setNamaKlien(dto.getNamaKlien().trim());
        klien.setEmailKlien(dto.getEmailKlien().trim().toLowerCase());
        klien.setNoTelp(dto.getNoTelp());
        klien.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusKlien.BELUM);
        klien.setTglRequest(dto.getTglRequest() != null ? dto.getTglRequest() : new Date());
        klien.setIsDeleted(false);

        Klien saved = klienRepository.save(klien);
        log.info("Klien created: id={}, email={}", saved.getIdKlien(), saved.getEmailKlien());

        return convertToDTO(saved);
    }

    /**
     * Method to update klien data
     */
    @Transactional
    public KlienDTO updateKlien(Integer id, KlienDTO dto) {
        Klien klien = klienRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Klien dengan ID " + id + " tidak ditemukan"));

        if (Boolean.TRUE.equals(klien.getIsDeleted())) {
            throw new ResourceNotFoundException(
                "Klien tidak dapat diupdate karena sudah dihapus");
        }

        klienRepository.findByEmailKlien(dto.getEmailKlien()).ifPresent(existing -> {
            if (!existing.getIdKlien().equals(id)) {
                throw new IllegalArgumentException(
                    "Email '" + dto.getEmailKlien() + "' sudah digunakan klien lain");
            }
        });

        klien.setNamaKlien(dto.getNamaKlien().trim());
        klien.setEmailKlien(dto.getEmailKlien().trim().toLowerCase());
        klien.setNoTelp(dto.getNoTelp());
        if (dto.getStatus() != null) {
            klien.setStatus(dto.getStatus());
        }

        Klien updated = klienRepository.save(klien);
        log.info("Klien updated: id={}", updated.getIdKlien());

        return convertToDTO(updated);
    }

    /**
     * Soft delete klien data
     */
    @Transactional
    public void deleteKlien(Integer id) {
        Klien klien = klienRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Klien dengan ID " + id + " tidak ditemukan"));

        if (Boolean.TRUE.equals(klien.getIsDeleted())) {
            throw new ResourceNotFoundException("Klien sudah dihapus sebelumnya");
        }

        klien.setIsDeleted(true);
        klienRepository.save(klien);
        log.info("Klien soft deleted: id={}", id);
    }

    public List<KlienDTO> searchKlien(String keyword, StatusKlien status) {
        List<Klien> klien = klienRepository.findAll().stream()
            .filter(k -> !Boolean.TRUE.equals(k.getIsDeleted()))
            .collect(Collectors.toList());

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            klien = klien.stream()
                .filter(k -> k.getNamaKlien().toLowerCase().contains(kw) ||
                    k.getEmailKlien().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        }

        if (status != null) {
            klien = klien.stream()
                .filter(k -> k.getStatus() == status)
                .collect(Collectors.toList());
        }

        return klien.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Update klien status
     */
    @Transactional
    public KlienDTO updateKlienStatus(Integer id, StatusKlien status) {
        Klien klien = klienRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Klien dengan ID " + id + " tidak ditemukan"));

        if (Boolean.TRUE.equals(klien.getIsDeleted())) {
            throw new ResourceNotFoundException(
                "Klien tidak dapat diupdate karena sudah dihapus");
        }

        klien.setStatus(status);
        Klien updated = klienRepository.save(klien);
        log.info("Klien status updated: id={}, status={}", id, status);

        return convertToDTO(updated);
    }

    private KlienDTO convertToDTO(Klien klien) {
        KlienDTO dto = new KlienDTO();
        dto.setIdKlien(klien.getIdKlien());
        dto.setNamaKlien(klien.getNamaKlien());
        dto.setEmailKlien(klien.getEmailKlien());
        dto.setNoTelp(klien.getNoTelp());
        dto.setStatus(klien.getStatus());
        dto.setTglRequest(klien.getTglRequest());

        if (klien.getManagerSet() != null && !klien.getManagerSet().isEmpty()) {
            Manager lastManager = klien.getManagerSet().stream().findFirst().orElse(null);
            if (lastManager != null) {
                dto.setLastApprovedBy(lastManager.getNamaManager());
            }
        }

        return dto;
    }
}