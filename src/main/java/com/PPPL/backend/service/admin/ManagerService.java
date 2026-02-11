package com.PPPL.backend.service.admin;

import com.PPPL.backend.data.admin.ManagerDTO;
import com.PPPL.backend.model.admin.Admin;
import com.PPPL.backend.model.admin.Manager;
import com.PPPL.backend.model.layanan.Layanan;
import com.PPPL.backend.repository.admin.AdminRepository;
import com.PPPL.backend.repository.admin.ManagerRepository;
import com.PPPL.backend.repository.layanan.LayananRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManagerService {
    
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private LayananRepository layananRepository;

    @Autowired
    private AdminRepository adminRepository;
    
    /**
     * Get all managers
     */
    public List<ManagerDTO> getAllManagers() {
        return managerRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get manager by ID
     */
    public ManagerDTO getManagerById(Integer id) {
        Manager manager = managerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Manager dengan ID " + id + " tidak ditemukan"));
        
        return convertToDTO(manager);
    }
    
    /**
     * Create new manager
     */
    public ManagerDTO createManager(ManagerDTO dto) {
        // Validasi
        if (dto.getNamaManager() == null || dto.getNamaManager().trim().isEmpty()) {
            throw new RuntimeException("Nama manager wajib diisi");
        }
        
        if (dto.getEmailManager() == null || dto.getEmailManager().trim().isEmpty()) {
            throw new RuntimeException("Email wajib diisi");
        }
        
        // Check duplicate email
        if (managerRepository.existsByEmailManager(dto.getEmailManager())) {
            throw new RuntimeException("Email sudah terdaftar");
        }
        
        // Create entity
        Manager manager = new Manager();
        manager.setNamaManager(dto.getNamaManager());
        manager.setEmailManager(dto.getEmailManager());
        manager.setNoTelp(dto.getNoTelp());
        manager.setDivisi(dto.getDivisi());
        manager.setTglMulai(dto.getTglMulai());
        
        Manager saved = managerRepository.save(manager);
        
        return convertToDTO(saved);
    }
    
    /**
     * Update manager
     */
    public ManagerDTO updateManager(Integer id, ManagerDTO dto) {
        Manager manager = managerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Manager dengan ID " + id + " tidak ditemukan"));
        
        // Check duplicate email (exclude current)
        if (!manager.getEmailManager().equals(dto.getEmailManager()) && 
            managerRepository.existsByEmailManager(dto.getEmailManager())) {
            throw new RuntimeException("Email sudah terdaftar");
        }
        
        // Update fields
        manager.setNamaManager(dto.getNamaManager());
        manager.setEmailManager(dto.getEmailManager());
        manager.setNoTelp(dto.getNoTelp());
        manager.setDivisi(dto.getDivisi());
        manager.setTglMulai(dto.getTglMulai());
        
        Manager updated = managerRepository.save(manager);
        
        return convertToDTO(updated);
    }
    
    /**
     * Delete manager - HARD DELETE from both table (Manager & Admin)
     */
    @Transactional
    public void deleteManager(Integer id) {
        Manager manager = managerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Manager dengan ID " + id + " tidak ditemukan"));
        
        String emailManager = manager.getEmailManager();
        
        // Check if manager has employees
        if (!manager.getKaryawanSet().isEmpty()) {
            throw new RuntimeException(
                "Manager tidak dapat dihapus karena masih memiliki " + 
                manager.getKaryawanSet().size() + " karyawan. Hapus atau pindahkan karyawan terlebih dahulu.");
        }
        
        // Check if manager has clients
        if (!manager.getKlienSet().isEmpty()) {
            throw new RuntimeException(
                "Manager tidak dapat dihapus karena masih menangani " + 
                manager.getKlienSet().size() + " klien. Hapus atau pindahkan klien terlebih dahulu.");
        }
        
        // 1. Delete from table Manager
        managerRepository.delete(manager);
        
        // 2. Delete from table Admin based on email
        Optional<Admin> adminOpt = adminRepository.findByEmail(emailManager);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            adminRepository.delete(admin);
            System.out.println("Admin dengan email " + emailManager + " berhasil dihapus");
        } else {
            System.out.println("Admin dengan email " + emailManager + " tidak ditemukan");
        }
    }
    
    /**
     * Search managers by name or divisi
     */
    public List<ManagerDTO> searchManagers(String keyword, String divisi) {
        List<Manager> managers = managerRepository.findAll();
        
        // Filter by keyword (name or email)
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase();
            managers = managers.stream()
                .filter(m -> m.getNamaManager().toLowerCase().contains(kw) ||
                           m.getEmailManager().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        }
        
        // Filter by divisi
        if (divisi != null && !divisi.trim().isEmpty()) {
            managers = managers.stream()
                .filter(m -> divisi.equalsIgnoreCase(m.getDivisi()))
                .collect(Collectors.toList());
        }
        
        return managers.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get unique divisi list
     */
    public List<String> getDivisiList() {
        return managerRepository.findAll()
            .stream()
            .map(Manager::getDivisi)
            .filter(d -> d != null && !d.trim().isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    /**
     * Get divisi list from nama layanan
     */
    public List<String> getDivisiFromLayanan() {
        return layananRepository.findAll()
            .stream()
            .map(Layanan::getNamaLayanan)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    // Helper methods
    private ManagerDTO convertToDTO(Manager manager) {
        ManagerDTO dto = new ManagerDTO();
        dto.setIdManager(manager.getIdManager());
        dto.setNamaManager(manager.getNamaManager());
        dto.setEmailManager(manager.getEmailManager());
        dto.setNoTelp(manager.getNoTelp());
        dto.setDivisi(manager.getDivisi());
        dto.setTglMulai(manager.getTglMulai());
        return dto;
    }
}