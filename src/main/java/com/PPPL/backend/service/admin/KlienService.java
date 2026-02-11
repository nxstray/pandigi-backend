package com.PPPL.backend.service.admin;

import com.PPPL.backend.data.client.KlienDTO;
import com.PPPL.backend.model.admin.Klien;
import com.PPPL.backend.model.admin.Manager;
import com.PPPL.backend.model.enums.StatusKlien;
import com.PPPL.backend.repository.client.KlienRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KlienService {
    
    @Autowired
    private KlienRepository klienRepository;
    
    /**
     * Get all klien - filter out deleted ones
     */
    public List<KlienDTO> getAllKlien() {
        return klienRepository
            .findKlienYangTerverifikasi()
            .stream()
            .filter(k -> !Boolean.TRUE.equals(k.getIsDeleted()))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get klien by ID
     */
    public KlienDTO getKlienById(Integer id) {
        Klien klien = klienRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Klien dengan ID " + id + " tidak ditemukan"));
        
        // Check if deleted
        if (Boolean.TRUE.equals(klien.getIsDeleted())) {
            throw new RuntimeException("Klien tidak ditemukan");
        }
        
        return convertToDTO(klien);
    }
    
    /**
     * Create new klien
     */
    public KlienDTO createKlien(KlienDTO dto) {
        // Validate
        if (dto.getNamaKlien() == null || dto.getNamaKlien().trim().isEmpty()) {
            throw new RuntimeException("Nama klien wajib diisi");
        }
        
        if (dto.getEmailKlien() == null || dto.getEmailKlien().trim().isEmpty()) {
            throw new RuntimeException("Email wajib diisi");
        }
        
        // Check duplicate email
        if (klienRepository.findByEmailKlien(dto.getEmailKlien()).isPresent()) {
            throw new RuntimeException("Email sudah terdaftar");
        }
        
        // Create entity
        Klien klien = new Klien();
        klien.setNamaKlien(dto.getNamaKlien());
        klien.setEmailKlien(dto.getEmailKlien());
        klien.setNoTelp(dto.getNoTelp());
        klien.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusKlien.BELUM);
        klien.setTglRequest(dto.getTglRequest() != null ? dto.getTglRequest() : new Date());
        klien.setIsDeleted(false);
        
        Klien saved = klienRepository.save(klien);
        
        return convertToDTO(saved);
    }
    
    /**
     * Update klien
     */
    public KlienDTO updateKlien(Integer id, KlienDTO dto) {
        Klien klien = klienRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Klien dengan ID " + id + " tidak ditemukan"));
        
        // Check if deleted
        if (Boolean.TRUE.equals(klien.getIsDeleted())) {
            throw new RuntimeException("Klien tidak dapat diupdate karena sudah dihapus");
        }
        
        // Check duplicate email (exclude current)
        klienRepository.findByEmailKlien(dto.getEmailKlien()).ifPresent(existing -> {
            if (!existing.getIdKlien().equals(id)) {
                throw new RuntimeException("Email sudah terdaftar");
            }
        });
        
        // Update fields
        klien.setNamaKlien(dto.getNamaKlien());
        klien.setEmailKlien(dto.getEmailKlien());
        klien.setNoTelp(dto.getNoTelp());
        if (dto.getStatus() != null) {
            klien.setStatus(dto.getStatus());
        }
        
        Klien updated = klienRepository.save(klien);
        
        return convertToDTO(updated);
    }
    
    /**
     * Delete klien (soft delete)
     */
    public void deleteKlien(Integer id) {
        // Find klien
        Klien klien = klienRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Klien dengan ID " + id + " tidak ditemukan"));
        
        // Check if already deleted
        if (Boolean.TRUE.equals(klien.getIsDeleted())) {
            throw new RuntimeException("Klien sudah dihapus sebelumnya");
        }
        
        // Soft delete
        klien.setIsDeleted(true);
        klienRepository.save(klien);
    }
    
    /**
     * Search klien - filter deleted
     */
    public List<KlienDTO> searchKlien(String keyword, StatusKlien status) {
        List<Klien> klien = klienRepository.findAll().stream()
            .filter(k -> !Boolean.TRUE.equals(k.getIsDeleted()))
            .collect(Collectors.toList());
        
        // Filter by keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase();
            klien = klien.stream()
                .filter(k -> k.getNamaKlien().toLowerCase().contains(kw) ||
                           k.getEmailKlien().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        }
        
        // Filter by status
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
    public KlienDTO updateKlienStatus(Integer id, StatusKlien status) {
        Klien klien = klienRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Klien dengan ID " + id + " tidak ditemukan"));
        
        // Check if deleted
        if (Boolean.TRUE.equals(klien.getIsDeleted())) {
            throw new RuntimeException("Klien tidak dapat diupdate karena sudah dihapus");
        }
        
        klien.setStatus(status);
        Klien updated = klienRepository.save(klien);
        
        return convertToDTO(updated);
    }
    
    // Helper methods   
    /**
     * Convert Klien entity to DTO - with info approved by manager
     */
    private KlienDTO convertToDTO(Klien klien) {
        KlienDTO dto = new KlienDTO();
        dto.setIdKlien(klien.getIdKlien());
        dto.setNamaKlien(klien.getNamaKlien());
        dto.setEmailKlien(klien.getEmailKlien());
        dto.setNoTelp(klien.getNoTelp());
        dto.setStatus(klien.getStatus());
        dto.setTglRequest(klien.getTglRequest());
        
        // Get last approved manager
        if (klien.getManagerSet() != null && !klien.getManagerSet().isEmpty()) {
            Manager lastManager = klien.getManagerSet().stream()
                    .findFirst()
                    .orElse(null);
            
            if (lastManager != null) {
                dto.setLastApprovedBy(lastManager.getNamaManager());
            }
        }
        
        return dto;
    }
}