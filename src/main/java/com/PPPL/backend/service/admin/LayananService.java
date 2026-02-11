package com.PPPL.backend.service.admin;

import com.PPPL.backend.data.layanan.LayananDTO;
import com.PPPL.backend.model.enums.KategoriLayanan;
import com.PPPL.backend.model.layanan.Layanan;
import com.PPPL.backend.repository.layanan.LayananRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LayananService {
    
    @Autowired
    private LayananRepository layananRepository;
    
    /**
     * Get all layanan
     */
    public List<LayananDTO> getAllLayanan() {
        return layananRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get layanan by ID
     */
    public LayananDTO getLayananById(Integer id) {
        Layanan layanan = layananRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Layanan dengan ID " + id + " tidak ditemukan"));
        
        return convertToDTO(layanan);
    }
    
    /**
     * Create new layanan
     */
    public LayananDTO createLayanan(LayananDTO dto) {
        // Validasi
        if (dto.getNamaLayanan() == null || dto.getNamaLayanan().trim().isEmpty()) {
            throw new RuntimeException("Nama layanan wajib diisi");
        }
        
        if (dto.getKategori() == null) {
            throw new RuntimeException("Kategori wajib dipilih");
        }
        
        // Check duplicate name
        List<Layanan> existing = layananRepository
                .findByNamaLayananContainingIgnoreCase(dto.getNamaLayanan());

        if (!existing.isEmpty()) {
            throw new RuntimeException("Nama layanan sudah terdaftar");
        }
        
        // Create entity
        Layanan layanan = new Layanan();
        layanan.setNamaLayanan(dto.getNamaLayanan());
        layanan.setKategori(dto.getKategori());
        layanan.setCatatan(dto.getCatatan());
        
        Layanan saved = layananRepository.save(layanan);
        
        return convertToDTO(saved);
    }
    
    /**
     * Update layanan
     */
    public LayananDTO updateLayanan(Integer id, LayananDTO dto) {
        Layanan layanan = layananRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Layanan dengan ID " + id + " tidak ditemukan"));
        
        // Check duplicate name (exclude current)
        List<Layanan> existingList = layananRepository
                .findByNamaLayananContainingIgnoreCase(dto.getNamaLayanan());

        for (Layanan existing : existingList) {
            if (!existing.getIdLayanan().equals(id)) {
                throw new RuntimeException("Nama layanan sudah terdaftar");
            }
        }
        
        // Update fields
        layanan.setNamaLayanan(dto.getNamaLayanan());
        layanan.setKategori(dto.getKategori());
        layanan.setCatatan(dto.getCatatan());
        
        Layanan updated = layananRepository.save(layanan);
        
        return convertToDTO(updated);
    }
    
    /**
     * Delete layanan
     */
    public void deleteLayanan(Integer id) {
        Layanan layanan = layananRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Layanan dengan ID " + id + " tidak ditemukan"));
        
        // Check if layanan has requests
        if (!layanan.getRequestLayananSet().isEmpty()) {
            throw new RuntimeException(
                "Layanan tidak dapat dihapus karena masih memiliki " + 
                layanan.getRequestLayananSet().size() + " request");
        }
        
        layananRepository.delete(layanan);
    }
    
    /**
     * Search layanan
     */
    public List<LayananDTO> searchLayanan(String keyword, KategoriLayanan kategori) {
        List<Layanan> layanan = layananRepository.findAll();
        
        // Filter by keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase();
            layanan = layanan.stream()
                .filter(l -> l.getNamaLayanan().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        }
        
        // Filter by kategori
        if (kategori != null) {
            layanan = layanan.stream()
                .filter(l -> l.getKategori() == kategori)
                .collect(Collectors.toList());
        }
        
        return layanan.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get layanan by kategori
     */
    public List<LayananDTO> getLayananByKategori(KategoriLayanan kategori) {
        return layananRepository.findAll()
            .stream()
            .filter(l -> l.getKategori() == kategori)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // Helper methods
    private LayananDTO convertToDTO(Layanan layanan) {
        LayananDTO dto = new LayananDTO();
        dto.setIdLayanan(layanan.getIdLayanan());
        dto.setNamaLayanan(layanan.getNamaLayanan());
        dto.setKategori(layanan.getKategori());
        dto.setCatatan(layanan.getCatatan());
        return dto;
    }
}