package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.data.rekap.RekapDTO;
import com.PPPL.backend.model.admin.Klien;
import com.PPPL.backend.model.enums.StatusRekap;
import com.PPPL.backend.model.layanan.Layanan;
import com.PPPL.backend.model.rekap.Rekap;
import com.PPPL.backend.repository.client.KlienRepository;
import com.PPPL.backend.repository.layanan.LayananRepository;
import com.PPPL.backend.repository.rekap.RekapRepository;
import com.PPPL.backend.security.AuthUser;
import com.PPPL.backend.service.rekap.RekapService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/rekap")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
public class RekapController {
    
    @Autowired
    private RekapRepository rekapRepository;
    
    @Autowired
    private KlienRepository klienRepository;
    
    @Autowired
    private LayananRepository layananRepository;

    @Autowired
    private RekapService rekapService;
    
    /**
     * Get all rekap
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RekapDTO>>> getAllRekap() {
        try {
            List<RekapDTO> rekap = rekapRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .sorted((a, b) -> b.getTglMeeting().compareTo(a.getTglMeeting())) // Newest first
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(rekap));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat data rekap: " + e.getMessage()));
        }
    }
    
    /**
     * Get rekap by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RekapDTO>> getRekapById(@PathVariable Integer id) {
        try {
            Rekap rekap = rekapRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rekap dengan ID " + id + " tidak ditemukan"));
            
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(rekap)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Create new rekap
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<RekapDTO>> createRekap(@RequestBody RekapDTO dto) {
        try {
            // Validasi
            if (dto.getIdKlien() == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Klien wajib dipilih"));
            }
            
            if (dto.getNamaManagerManual() == null || dto.getNamaManagerManual().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Nama manager wajib diisi"));
            }
            
            if (dto.getIdLayanan() == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Layanan wajib dipilih"));
            }
            
            if (dto.getTglMeeting() == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tanggal meeting wajib diisi"));
            }
            
            // Get entities
            Klien klien = klienRepository.findById(dto.getIdKlien())
                .orElseThrow(() -> new RuntimeException("Klien tidak ditemukan"));
            
            Layanan layanan = layananRepository.findById(dto.getIdLayanan())
                .orElseThrow(() -> new RuntimeException("Layanan tidak ditemukan"));
            
            // Create entity
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
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rekap meeting berhasil ditambahkan", convertToDTO(saved)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal menambah rekap: " + e.getMessage()));
        }
    }
    
    /**
     * Update rekap
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<RekapDTO>> updateRekap(
            @PathVariable Integer id,
            @RequestBody RekapDTO dto
    ) {
        try {
            AuthUser auth = AuthUser.fromContext();
            Rekap updated = rekapService.updateRekap(id, dto, auth);
            
            return ResponseEntity.ok(
                ApiResponse.success("Rekap meeting berhasil diupdate", convertToDTO(updated))
            );
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal update rekap: " + e.getMessage()));
        }
    }
    
    /**
     * Delete rekap
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRekap(@PathVariable Integer id) {
        try {
            Rekap rekap = rekapRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rekap dengan ID " + id + " tidak ditemukan"));
            
            rekapRepository.delete(rekap);
            
            return ResponseEntity.ok(ApiResponse.success("Rekap meeting berhasil dihapus", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal hapus rekap: " + e.getMessage()));
        }
    }
    
    /**
     * Search rekap
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RekapDTO>>> searchRekap(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) StatusRekap status) {
        try {
            List<Rekap> rekap = rekapRepository.findAll();
            
            // Filter by keyword (klien or manager name)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.toLowerCase();
                rekap = rekap.stream()
                    .filter(r -> {
                        boolean matchKlien = r.getKlien().getNamaKlien().toLowerCase().contains(kw);
                        
                        // Cek nama manager manual dulu, kalau null baru cek manager entity
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
            
            // Filter by status
            if (status != null) {
                rekap = rekap.stream()
                    .filter(r -> r.getStatus() == status)
                    .collect(Collectors.toList());
            }
            
            List<RekapDTO> result = rekap.stream()
                .map(this::convertToDTO)
                .sorted((a, b) -> b.getTglMeeting().compareTo(a.getTglMeeting()))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal search rekap: " + e.getMessage()));
        }
    }
    
    /**
     * Get rekap by klien
     */
    @GetMapping("/klien/{idKlien}")
    public ResponseEntity<ApiResponse<List<RekapDTO>>> getRekapByKlien(@PathVariable Integer idKlien) {
        try {
            List<RekapDTO> rekap = rekapRepository.findAll()
                .stream()
                .filter(r -> r.getKlien().getIdKlien().equals(idKlien))
                .map(this::convertToDTO)
                .sorted((a, b) -> b.getTglMeeting().compareTo(a.getTglMeeting()))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(rekap));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal load rekap: " + e.getMessage()));
        }
    }
    
    /**
     * Get rekap by manager
     */
    @GetMapping("/manager/{idManager}")
    public ResponseEntity<ApiResponse<List<RekapDTO>>> getRekapByManager(@PathVariable Integer idManager) {
        try {
            List<RekapDTO> rekap = rekapRepository.findAll()
                .stream()
                .filter(r -> r.getManager() != null && r.getManager().getIdManager().equals(idManager))
                .map(this::convertToDTO)
                .sorted((a, b) -> b.getTglMeeting().compareTo(a.getTglMeeting()))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(rekap));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal load rekap: " + e.getMessage()));
        }
    }
    
    // ========== HELPER METHODS ==========
    private RekapDTO convertToDTO(Rekap rekap) {
        RekapDTO dto = new RekapDTO();
        dto.setIdMeeting(rekap.getIdMeeting());
        dto.setIdKlien(rekap.getKlien().getIdKlien());
        dto.setNamaKlien(rekap.getKlien().getNamaKlien());
        
        // Prioritaskan namaManagerManual, fallback ke manager entity untuk data lama
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