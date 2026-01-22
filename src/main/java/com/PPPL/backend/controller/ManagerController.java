package com.PPPL.backend.controller;

import com.PPPL.backend.data.ApiResponse;
import com.PPPL.backend.data.ManagerDTO;
import com.PPPL.backend.model.Admin;
import com.PPPL.backend.model.Layanan;
import com.PPPL.backend.model.Manager;
import com.PPPL.backend.repository.ManagerRepository;

import jakarta.transaction.Transactional;

import com.PPPL.backend.repository.AdminRepository;
import com.PPPL.backend.repository.LayananRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/manager")
@CrossOrigin(origins = "http://localhost:4200")
public class ManagerController {
    
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private LayananRepository layananRepository;

    @Autowired
    private AdminRepository adminRepository;
    
    /**
     * Get all managers
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ManagerDTO>>> getAllManagers() {
        try {
            List<ManagerDTO> managers = managerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(managers));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat data manager: " + e.getMessage()));
        }
    }
    
    /**
     * Get manager by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ManagerDTO>> getManagerById(@PathVariable Integer id) {
        try {
            Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager dengan ID " + id + " tidak ditemukan"));
            
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(manager)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Create new manager
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ManagerDTO>> createManager(@RequestBody ManagerDTO dto) {
        try {
            // Validasi
            if (dto.getNamaManager() == null || dto.getNamaManager().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Nama manager wajib diisi"));
            }
            
            if (dto.getEmailManager() == null || dto.getEmailManager().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email wajib diisi"));
            }
            
            // Check duplicate email
            if (managerRepository.existsByEmailManager(dto.getEmailManager())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email sudah terdaftar"));
            }
            
            // Create entity
            Manager manager = new Manager();
            manager.setNamaManager(dto.getNamaManager());
            manager.setEmailManager(dto.getEmailManager());
            manager.setNoTelp(dto.getNoTelp());
            manager.setDivisi(dto.getDivisi());
            manager.setTglMulai(dto.getTglMulai());
            
            Manager saved = managerRepository.save(manager);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Manager berhasil ditambahkan", convertToDTO(saved)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal menambah manager: " + e.getMessage()));
        }
    }
    
    /**
     * Update manager
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ManagerDTO>> updateManager(
            @PathVariable Integer id, 
            @RequestBody ManagerDTO dto) {
        try {
            Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager dengan ID " + id + " tidak ditemukan"));
            
            // Check duplicate email (exclude current)
            if (!manager.getEmailManager().equals(dto.getEmailManager()) && 
                managerRepository.existsByEmailManager(dto.getEmailManager())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email sudah terdaftar"));
            }
            
            // Update fields
            manager.setNamaManager(dto.getNamaManager());
            manager.setEmailManager(dto.getEmailManager());
            manager.setNoTelp(dto.getNoTelp());
            manager.setDivisi(dto.getDivisi());
            manager.setTglMulai(dto.getTglMulai());
            
            Manager updated = managerRepository.save(manager);
            
            return ResponseEntity.ok(
                ApiResponse.success("Manager berhasil diupdate", convertToDTO(updated)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal update manager: " + e.getMessage()));
        }
    }
    
    /**
     * Delete manager - HARD DELETE dari kedua tabel (Manager & Admin)
     */
    @Transactional
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteManager(@PathVariable Integer id) {
        try {
            Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager dengan ID " + id + " tidak ditemukan"));
            
            String emailManager = manager.getEmailManager();
            
            // Check if manager has employees
            if (!manager.getKaryawanSet().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Manager tidak dapat dihapus karena masih memiliki " + 
                        manager.getKaryawanSet().size() + " karyawan. Hapus atau pindahkan karyawan terlebih dahulu."));
            }
            
            // Check if manager has clients
            if (!manager.getKlienSet().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Manager tidak dapat dihapus karena masih menangani " + 
                        manager.getKlienSet().size() + " klien. Hapus atau pindahkan klien terlebih dahulu."));
            }
            
            // 1. Hapus dari tabel Manager
            managerRepository.delete(manager);
            
            // 2. Hapus dari tabel Admin berdasarkan email
            Optional<Admin> adminOpt = adminRepository.findByEmail(emailManager);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                adminRepository.delete(admin);
                System.out.println("Admin dengan email " + emailManager + " berhasil dihapus");
            } else {
                System.out.println("Admin dengan email " + emailManager + " tidak ditemukan");
            }
            
            return ResponseEntity.ok(ApiResponse.success("Manager dan akun login berhasil dihapus", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal hapus manager: " + e.getMessage()));
        }
    }
    
    /**
     * Search managers by name or divisi
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ManagerDTO>>> searchManagers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String divisi) {
        try {
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
            
            List<ManagerDTO> result = managers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal search manager: " + e.getMessage()));
        }
    }
    
    /**
     * Get unique divisi list
     */
    @GetMapping("/divisi")
    public ResponseEntity<ApiResponse<List<String>>> getDivisiList() {
        try {
            List<String> divisiList = managerRepository.findAll()
                .stream()
                .map(Manager::getDivisi)
                .filter(d -> d != null && !d.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(divisiList));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal load divisi: " + e.getMessage()));
        }
    }

    /**
     * Get divisi list from nama layanan
     */
    @GetMapping("/divisi-layanan")
    public ResponseEntity<ApiResponse<List<String>>> getDivisiFromLayanan() {
        try {
            List<String> divisiList = layananRepository.findAll()
                .stream()
                .map(Layanan::getNamaLayanan)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(divisiList));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal load divisi: " + e.getMessage()));
        }
    }
    
    // ========== HELPER METHODS ==========
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