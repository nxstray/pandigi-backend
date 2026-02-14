package com.PPPL.backend.service.admin;

import com.PPPL.backend.data.admin.ManagerDTO;
import com.PPPL.backend.handler.ResourceNotFoundException;
import com.PPPL.backend.model.admin.Admin;
import com.PPPL.backend.model.admin.Manager;
import com.PPPL.backend.model.layanan.Layanan;
import com.PPPL.backend.repository.admin.AdminRepository;
import com.PPPL.backend.repository.admin.ManagerRepository;
import com.PPPL.backend.repository.layanan.LayananRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManagerService {

    private static final Logger log = LoggerFactory.getLogger(ManagerService.class);

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private LayananRepository layananRepository;

    @Autowired
    private AdminRepository adminRepository;

    public List<ManagerDTO> getAllManagers() {
        return managerRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Method to get manager by ID
     */
    public ManagerDTO getManagerById(Integer id) {
        Manager manager = managerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Manager dengan ID " + id + " tidak ditemukan"));
        return convertToDTO(manager);
    }

    /**
     * Method to create new manager data
     */
    @Transactional
    public ManagerDTO createManager(ManagerDTO dto) {
        if (managerRepository.existsByEmailManager(dto.getEmailManager())) {
            throw new IllegalArgumentException(
                "Email '" + dto.getEmailManager() + "' sudah terdaftar");
        }

        Manager manager = new Manager();
        manager.setNamaManager(dto.getNamaManager().trim());
        manager.setEmailManager(dto.getEmailManager().trim().toLowerCase());
        manager.setNoTelp(dto.getNoTelp());
        manager.setDivisi(dto.getDivisi());
        manager.setTglMulai(dto.getTglMulai());

        Manager saved = managerRepository.save(manager);
        log.info("Manager created: id={}, email={}", saved.getIdManager(), saved.getEmailManager());

        return convertToDTO(saved);
    }

    /**
     * Method to update manager data
     */
    @Transactional
    public ManagerDTO updateManager(Integer id, ManagerDTO dto) {
        Manager manager = managerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Manager dengan ID " + id + " tidak ditemukan"));

        String emailBaru = dto.getEmailManager().trim().toLowerCase();
        if (!manager.getEmailManager().equals(emailBaru) &&
            managerRepository.existsByEmailManager(emailBaru)) {
            throw new IllegalArgumentException(
                "Email '" + emailBaru + "' sudah digunakan manager lain");
        }

        manager.setNamaManager(dto.getNamaManager().trim());
        manager.setEmailManager(emailBaru);
        manager.setNoTelp(dto.getNoTelp());
        manager.setDivisi(dto.getDivisi());
        manager.setTglMulai(dto.getTglMulai());

        Manager updated = managerRepository.save(manager);
        log.info("Manager updated: id={}", updated.getIdManager());

        return convertToDTO(updated);
    }

    /**
     * Method to to hard delete manager by ID
     */
    @Transactional
    public void deleteManager(Integer id) {
        Manager manager = managerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Manager dengan ID " + id + " tidak ditemukan"));

        if (!manager.getKaryawanSet().isEmpty()) {
            throw new IllegalStateException(
                "Manager tidak dapat dihapus karena masih memiliki " +
                manager.getKaryawanSet().size() +
                " karyawan. Hapus atau pindahkan karyawan terlebih dahulu.");
        }

        if (!manager.getKlienSet().isEmpty()) {
            throw new IllegalStateException(
                "Manager tidak dapat dihapus karena masih menangani " +
                manager.getKlienSet().size() +
                " klien. Hapus atau pindahkan klien terlebih dahulu.");
        }

        String emailManager = manager.getEmailManager();
        managerRepository.delete(manager);

        Optional<Admin> adminOpt = adminRepository.findByEmail(emailManager);
        if (adminOpt.isPresent()) {
            adminRepository.delete(adminOpt.get());
            log.info("Admin terkait dihapus: email={}", emailManager);
        } else {
            log.warn("Admin terkait tidak ditemukan: email={}", emailManager);
        }

        log.info("Manager deleted: id={}", id);
    }

    public List<ManagerDTO> searchManagers(String keyword, String divisi) {
        List<Manager> managers = managerRepository.findAll();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            managers = managers.stream()
                .filter(m -> m.getNamaManager().toLowerCase().contains(kw) ||
                    m.getEmailManager().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        }

        if (divisi != null && !divisi.trim().isEmpty()) {
            managers = managers.stream()
                .filter(m -> divisi.equalsIgnoreCase(m.getDivisi()))
                .collect(Collectors.toList());
        }

        return managers.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<String> getDivisiList() {
        return managerRepository.findAll()
            .stream()
            .map(Manager::getDivisi)
            .filter(d -> d != null && !d.trim().isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    public List<String> getDivisiFromLayanan() {
        return layananRepository.findAll()
            .stream()
            .map(Layanan::getNamaLayanan)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

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