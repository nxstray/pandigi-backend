package com.PPPL.backend.controller;

import com.PPPL.backend.data.*;
import com.PPPL.backend.model.*;
import com.PPPL.backend.repository.KlienRepository;
import com.PPPL.backend.security.AuthUser;
import com.PPPL.backend.service.RequestLayananService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/request-layanan")
@CrossOrigin(origins = "http://localhost:4200")
public class RequestLayananController {

    @Autowired
    private RequestLayananService requestLayananService;

    @Autowired
    private KlienRepository klienRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RequestLayananDTO>>> getAll() {
        List<RequestLayananDTO> data = requestLayananService.findAll()
                .stream()
                .map(this::toDTO)
                .sorted(Comparator.comparing(RequestLayananDTO::getTglRequest).reversed())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RequestLayananDetailDTO>> getById(@PathVariable Integer id) {
        RequestLayanan r = requestLayananService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(toDetailDTO(r)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<RequestLayananDTO>>> byStatus(
            @PathVariable StatusRequest status) {

        List<RequestLayananDTO> data = requestLayananService.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<RequestLayananStatisticsDTO>> statistics() {
        return ResponseEntity.ok(ApiResponse.success(
                requestLayananService.getStatistics()
        ));
    }

    @GetMapping("/active-klien")
    public ResponseEntity<ApiResponse<List<Klien>>> getActiveKlien() {
        return ResponseEntity.ok(ApiResponse.success(
                klienRepository.findKlienYangTerverifikasi()
        ));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<RequestLayananDTO>> approve(@PathVariable Integer id) {

        AuthUser auth = AuthUser.fromContext();

        RequestLayanan approved =
                requestLayananService.approve(id, auth.userId(), auth.role());

        return ResponseEntity.ok(
                ApiResponse.success("Request berhasil diverifikasi", toDTO(approved))
        );
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<RequestLayananDTO>> reject(
            @PathVariable Integer id,
            @RequestBody RejectRequest body) {

        RequestLayanan rejected =
                requestLayananService.reject(id, body.keterangan);

        return ResponseEntity.ok(
                ApiResponse.success("Request berhasil ditolak", toDTO(rejected))
        );
    }

    // ================== MAPPERS ==================

    private RequestLayananDTO toDTO(RequestLayanan r) {
        RequestLayananDTO dto = new RequestLayananDTO();
        dto.setIdRequest(r.getIdRequest());
        dto.setIdLayanan(r.getLayanan().getIdLayanan());
        dto.setNamaLayanan(r.getLayanan().getNamaLayanan());
        dto.setIdKlien(r.getKlien().getIdKlien());
        dto.setNamaKlien(r.getKlien().getNamaKlien());
        dto.setTglRequest(r.getTglRequest());
        dto.setStatus(r.getStatus());
        dto.setTglVerifikasi(r.getTglVerifikasi());
        dto.setKeteranganPenolakan(r.getKeteranganPenolakan());

        if (r.getApprovedByManager() != null) {
            dto.setApprovedByManagerId(r.getApprovedByManager().getIdManager());
            dto.setApprovedByName(r.getApprovedByName());
        }

        return dto;
    }

    private RequestLayananDetailDTO toDetailDTO(RequestLayanan r) {
        RequestLayananDetailDTO dto = new RequestLayananDetailDTO();
        dto.setIdRequest(r.getIdRequest());
        dto.setTglRequest(r.getTglRequest());
        dto.setStatus(r.getStatus());
        dto.setTglVerifikasi(r.getTglVerifikasi());
        dto.setKeteranganPenolakan(r.getKeteranganPenolakan());

        dto.setIdKlien(r.getKlien().getIdKlien());
        dto.setNamaKlien(r.getKlien().getNamaKlien());
        dto.setEmailKlien(r.getKlien().getEmailKlien());
        dto.setNoTelpKlien(r.getKlien().getNoTelp());
        dto.setPerusahaan(r.getPerusahaan());

        dto.setIdLayanan(r.getLayanan().getIdLayanan());
        dto.setNamaLayanan(r.getLayanan().getNamaLayanan());
        dto.setKategoriLayanan(r.getLayanan().getKategori().name());

        dto.setPesan(r.getPesan());
        dto.setAnggaran(r.getAnggaran());
        dto.setWaktuImplementasi(r.getWaktuImplementasi());

        dto.setAiAnalyzed(r.getAiAnalyzed());
        dto.setSkorPrioritas(r.getSkorPrioritas());
        dto.setKategoriLead(r.getKategoriLead());
        dto.setAlasanSkor(r.getAlasanSkor());

        return dto;
    }

    public static class RejectRequest {
        public String keterangan;
    }
}
