package com.PPPL.backend.service.admin;

import com.PPPL.backend.data.admin.KaryawanDTO;
import com.PPPL.backend.model.admin.Karyawan;
import com.PPPL.backend.model.admin.Manager;
import com.PPPL.backend.repository.admin.KaryawanRepository;
import com.PPPL.backend.repository.admin.ManagerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KaryawanService {
    
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
            .orElseThrow(() -> new RuntimeException("Karyawan dengan ID " + id + " tidak ditemukan"));
        
        return convertToDTO(karyawan);
    }
    
    /**
     * Create new karyawan
     */
    public KaryawanDTO createKaryawan(KaryawanDTO dto) {
        // Validasi
        if (dto.getNamaKaryawan() == null || dto.getNamaKaryawan().trim().isEmpty()) {
            throw new RuntimeException("Nama karyawan wajib diisi");
        }
        
        if (dto.getEmailKaryawan() == null || dto.getEmailKaryawan().trim().isEmpty()) {
            throw new RuntimeException("Email wajib diisi");
        }
        
        if (dto.getIdManager() == null) {
            throw new RuntimeException("Manager wajib dipilih");
        }
        
        // Check duplicate email
        if (karyawanRepository.existsByEmailKaryawan(dto.getEmailKaryawan())) {
            throw new RuntimeException("Email sudah terdaftar");
        }
        
        // Get manager
        Manager manager = managerRepository.findById(dto.getIdManager())
            .orElseThrow(() -> new RuntimeException("Manager tidak ditemukan"));
        
        // Create entity
        Karyawan karyawan = new Karyawan();
        karyawan.setNamaKaryawan(dto.getNamaKaryawan());
        karyawan.setEmailKaryawan(dto.getEmailKaryawan());
        karyawan.setNoTelp(dto.getNoTelp());
        karyawan.setJabatanPosisi(dto.getJabatanPosisi());
        karyawan.setManager(manager);
        karyawan.setFotoProfil(dto.getFotoProfil());
        
        Karyawan saved = karyawanRepository.save(karyawan);
        
        System.out.println("Karyawan created with foto: " + (saved.getFotoProfil() != null ? "YES" : "NO"));
        
        return convertToDTO(saved);
    }
    
    /**
     * Update karyawan
     */
    public KaryawanDTO updateKaryawan(Integer id, KaryawanDTO dto) {
        Karyawan karyawan = karyawanRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Karyawan dengan ID " + id + " tidak ditemukan"));
        
        // Check duplicate email (exclude current)
        if (!karyawan.getEmailKaryawan().equals(dto.getEmailKaryawan()) && 
            karyawanRepository.existsByEmailKaryawan(dto.getEmailKaryawan())) {
            throw new RuntimeException("Email sudah terdaftar");
        }
        
        // Get manager
        Manager manager = managerRepository.findById(dto.getIdManager())
            .orElseThrow(() -> new RuntimeException("Manager tidak ditemukan"));
        
        // Update fields
        karyawan.setNamaKaryawan(dto.getNamaKaryawan());
        karyawan.setEmailKaryawan(dto.getEmailKaryawan());
        karyawan.setNoTelp(dto.getNoTelp());
        karyawan.setJabatanPosisi(dto.getJabatanPosisi());
        karyawan.setManager(manager);
        
        // Update foto profil (only if provided)
        if (dto.getFotoProfil() != null) {
            karyawan.setFotoProfil(dto.getFotoProfil());
        }
        
        Karyawan updated = karyawanRepository.save(karyawan);
        
        System.out.println("Karyawan updated with foto: " + (updated.getFotoProfil() != null ? "YES" : "NO"));
        
        return convertToDTO(updated);
    }
    
    /**
     * Delete karyawan
     */
    public void deleteKaryawan(Integer id) {
        if (!karyawanRepository.existsById(id)) {
            throw new RuntimeException("Karyawan dengan ID " + id + " tidak ditemukan");
        }
        
        karyawanRepository.deleteById(id);
    }
    
    /**
     * Search karyawan
     */
    public List<KaryawanDTO> searchKaryawan(String keyword, Integer idManager) {
        List<Karyawan> karyawan = karyawanRepository.findAll();
        
        // Filter by keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase();
            karyawan = karyawan.stream()
                .filter(k -> k.getNamaKaryawan().toLowerCase().contains(kw) ||
                           k.getEmailKaryawan().toLowerCase().contains(kw) ||
                           k.getJabatanPosisi().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        }
        
        // Filter by manager
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
        return karyawanRepository.findAll()
            .stream()
            .filter(k -> k.getManager().getIdManager().equals(idManager))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // Helper methods
    /**
     * Convert to DTO
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