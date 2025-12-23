package com.PPPL.backend.controller;

import com.PPPL.backend.data.ApiResponse;
import com.PPPL.backend.data.ClientFormDTO;
import com.PPPL.backend.model.Klien;
import com.PPPL.backend.model.Layanan;
import com.PPPL.backend.model.RequestLayanan;
import com.PPPL.backend.model.StatusKlien;
import com.PPPL.backend.model.StatusRequest;
import com.PPPL.backend.repository.KlienRepository;
import com.PPPL.backend.repository.LayananRepository;
import com.PPPL.backend.repository.RequestLayananRepository;
import com.PPPL.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/form")
@CrossOrigin(origins = "*")
public class ClientFormController {
    
    @Autowired
    private KlienRepository klienRepository;
    
    @Autowired
    private LayananRepository layananRepository;
    
    @Autowired
    private RequestLayananRepository requestLayananRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Submit form dari client - otomatis create Klien + RequestLayanan
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<RequestSubmitResponse>> submitForm(@RequestBody ClientFormDTO form) {
        try {
            // 1. Validasi input
            if (form.getEmail() == null || form.getEmail().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email wajib diisi"));
            }
            
            if (form.getIdLayanan() == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Layanan wajib dipilih"));
            }
            
            // 2. Check apakah klien sudah ada (berdasarkan email)
            Klien klien = klienRepository.findByEmailKlien(form.getEmail())
                .orElse(null);
            
            // 3. Jika belum ada, buat klien baru
            boolean isNewClient = false;
            if (klien == null) {
                klien = new Klien();
                klien.setNamaKlien(form.getFullName());
                klien.setEmailKlien(form.getEmail());
                klien.setNoTelp(form.getPhoneNumber());
                klien.setStatus(StatusKlien.BELUM);
                klien.setTglRequest(new Date());
                klien = klienRepository.save(klien);
                isNewClient = true;
            }
            
            // 4. Cari layanan berdasarkan ID
            Layanan layanan = layananRepository.findById(form.getIdLayanan())
                .orElseThrow(() -> new RuntimeException("Layanan dengan ID " + form.getIdLayanan() + " tidak ditemukan"));
            
            // 5. Buat request layanan
            RequestLayanan request = new RequestLayanan();
            request.setKlien(klien);
            request.setLayanan(layanan);
            request.setTglRequest(new Date());
            request.setStatus(StatusRequest.MENUNGGU_VERIFIKASI);
            
            // Mapping field dengan benar
            request.setPerusahaan(form.getPerusahaan());
            request.setTopic(layanan.getNamaLayanan());
            request.setPesan(form.getMessage());
            request.setAnggaran(form.getAnggaran());
            request.setWaktuImplementasi(form.getWaktuImplementasi());
            request.setAiAnalyzed(false); // Belum dianalisis
            
            RequestLayanan savedRequest = requestLayananRepository.save(request);
            
            // 6. CREATE NOTIFICATIONS
            if (isNewClient) {
                // Notification for new client
                notificationService.createNotification(
                    "NEW_CLIENT",
                    "Klien Baru: " + klien.getNamaKlien(),
                    "Klien baru telah mendaftar untuk layanan " + layanan.getNamaLayanan(),
                    "/admin/klien"
                );
            }
            
            // Notification for pending verification
            notificationService.createNotification(
                "PENDING_VERIFICATION",
                "Request Perlu Verifikasi",
                klien.getNamaKlien() + " meminta layanan " + layanan.getNamaLayanan() + ". Menunggu verifikasi.",
                "/admin/request-layanan"
            );
            
            // 7. Return success dengan data response
            RequestSubmitResponse response = new RequestSubmitResponse(
                savedRequest.getIdRequest(),
                klien.getNamaKlien(),
                layanan.getNamaLayanan(),
                isNewClient
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    "Form berhasil terkirim! Tim kami akan segera menghubungi anda.", 
                    response
                ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Terjadi kesalahan: " + e.getMessage()));
        }
    }
    
    /**
     * Get daftar layanan untuk dropdown
     */
    @GetMapping("/layanan")
    public ResponseEntity<ApiResponse<List<LayananOption>>> getLayananOptions() {
        List<LayananOption> options = layananRepository.findAll()
            .stream()
            .map(l -> new LayananOption(l.getIdLayanan(), l.getNamaLayanan(), l.getKategori().toString()))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(options));
    }
    
    public static class LayananOption {
        public Integer id;
        public String name;
        public String category;
        
        public LayananOption(Integer id, String name, String category) {
            this.id = id;
            this.name = name;
            this.category = category;
        }
    }
    
    public static class RequestSubmitResponse {
        public Integer idRequest;
        public String clientName;
        public String serviceName;
        public boolean isNewClient;
        
        public RequestSubmitResponse(Integer idRequest, String clientName, String serviceName, boolean isNewClient) {
            this.idRequest = idRequest;
            this.clientName = clientName;
            this.serviceName = serviceName;
            this.isNewClient = isNewClient;
        }
    }
}