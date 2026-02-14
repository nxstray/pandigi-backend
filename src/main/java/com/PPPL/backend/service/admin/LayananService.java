package com.PPPL.backend.service.admin;

import com.PPPL.backend.data.layanan.LayananDTO;
import com.PPPL.backend.handler.ResourceNotFoundException;
import com.PPPL.backend.model.enums.KategoriLayanan;
import com.PPPL.backend.model.layanan.Layanan;
import com.PPPL.backend.repository.layanan.LayananRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LayananService {

    private static final Logger log = LoggerFactory.getLogger(LayananService.class);

    @Autowired
    private LayananRepository layananRepository;

    public List<LayananDTO> getAllLayanan() {
        return layananRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Method to get layanan by ID
     */
    public LayananDTO getLayananById(Integer id) {
        Layanan layanan = layananRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Layanan dengan ID " + id + " tidak ditemukan"));
        return convertToDTO(layanan);
    }

    /**
     * Method to create new layanan data
     */
    @Transactional
    public LayananDTO createLayanan(LayananDTO dto) {
        List<Layanan> existing = layananRepository
            .findByNamaLayananContainingIgnoreCase(dto.getNamaLayanan());

        if (!existing.isEmpty()) {
            throw new IllegalArgumentException(
                "Nama layanan '" + dto.getNamaLayanan() + "' sudah terdaftar");
        }

        Layanan layanan = new Layanan();
        layanan.setNamaLayanan(dto.getNamaLayanan().trim());
        layanan.setKategori(dto.getKategori());
        layanan.setCatatan(dto.getCatatan());

        Layanan saved = layananRepository.save(layanan);
        log.info("Layanan created: id={}, nama={}", saved.getIdLayanan(), saved.getNamaLayanan());

        return convertToDTO(saved);
    }

    /**
     * Method to update layanan data
     */
    @Transactional
    public LayananDTO updateLayanan(Integer id, LayananDTO dto) {
        Layanan layanan = layananRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Layanan dengan ID " + id + " tidak ditemukan"));

        List<Layanan> existingList = layananRepository
            .findByNamaLayananContainingIgnoreCase(dto.getNamaLayanan());

        for (Layanan existing : existingList) {
            if (!existing.getIdLayanan().equals(id)) {
                throw new IllegalArgumentException(
                    "Nama layanan '" + dto.getNamaLayanan() + "' sudah terdaftar");
            }
        }

        layanan.setNamaLayanan(dto.getNamaLayanan().trim());
        layanan.setKategori(dto.getKategori());
        layanan.setCatatan(dto.getCatatan());

        Layanan updated = layananRepository.save(layanan);
        log.info("Layanan updated: id={}, nama={}", updated.getIdLayanan(), updated.getNamaLayanan());

        return convertToDTO(updated);
    }

    /**
     * Method to delete layanan by ID
     */
    @Transactional
    public void deleteLayanan(Integer id) {
        Layanan layanan = layananRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Layanan dengan ID " + id + " tidak ditemukan"));

        if (!layanan.getRequestLayananSet().isEmpty()) {
            throw new IllegalStateException(
                "Layanan tidak dapat dihapus karena masih memiliki " +
                layanan.getRequestLayananSet().size() + " request aktif");
        }

        layananRepository.delete(layanan);
        log.info("Layanan deleted: id={}", id);
    }

    public List<LayananDTO> searchLayanan(String keyword, KategoriLayanan kategori) {
        List<Layanan> layanan = layananRepository.findAll();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            layanan = layanan.stream()
                .filter(l -> l.getNamaLayanan().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        }

        if (kategori != null) {
            layanan = layanan.stream()
                .filter(l -> l.getKategori() == kategori)
                .collect(Collectors.toList());
        }

        return layanan.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<LayananDTO> getLayananByKategori(KategoriLayanan kategori) {
        return layananRepository.findAll()
            .stream()
            .filter(l -> l.getKategori() == kategori)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private LayananDTO convertToDTO(Layanan layanan) {
        LayananDTO dto = new LayananDTO();
        dto.setIdLayanan(layanan.getIdLayanan());
        dto.setNamaLayanan(layanan.getNamaLayanan());
        dto.setKategori(layanan.getKategori());
        dto.setCatatan(layanan.getCatatan());
        return dto;
    }
}