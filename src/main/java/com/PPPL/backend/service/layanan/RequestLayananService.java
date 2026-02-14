package com.PPPL.backend.service.layanan;

import com.PPPL.backend.data.layanan.RequestLayananStatisticsDTO;
import com.PPPL.backend.event.NotificationEventPublisher;
import com.PPPL.backend.model.admin.Admin;
import com.PPPL.backend.model.admin.Klien;
import com.PPPL.backend.model.enums.StatusKlien;
import com.PPPL.backend.model.enums.StatusRequest;
import com.PPPL.backend.model.layanan.RequestLayanan;
import com.PPPL.backend.repository.admin.AdminRepository;
import com.PPPL.backend.repository.client.KlienRepository;
import com.PPPL.backend.repository.layanan.RequestLayananRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RequestLayananService {

    private final RequestLayananRepository requestLayananRepository;
    private final NotificationEventPublisher notificationPublisher;
    private final KlienRepository klienRepository;
    private final AdminRepository adminRepository;

    public RequestLayananService(
            RequestLayananRepository requestLayananRepository,
            NotificationEventPublisher notificationPublisher,
            KlienRepository klienRepository,
            AdminRepository adminRepository
    ) {
        this.requestLayananRepository = requestLayananRepository;
        this.notificationPublisher = notificationPublisher;
        this.klienRepository = klienRepository;
        this.adminRepository = adminRepository;
    }

    public List<RequestLayanan> findAll() {
        return requestLayananRepository.findAll();
    }

    public RequestLayanan findById(Integer id) {
        return requestLayananRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request layanan tidak ditemukan"));
    }

    public List<RequestLayanan> findByStatus(StatusRequest status) {
        return requestLayananRepository.findByStatus(status);
    }

    /**
     * Public method - dipanggil controller
     */
    public RequestLayananStatisticsDTO getStatistics() {
        return getCachedStatistics();
    }

    /**
     * Get statistics with Redis caching (5 minutes TTL)
     */
    @Cacheable(value = "requestStatistics", key = "'stats_v2'")
    private RequestLayananStatisticsDTO getCachedStatistics() {
        log.info("=== CACHE MISS: Calculating request statistics ===");
        
        long total = requestLayananRepository.count();
        long menungguVerifikasi = requestLayananRepository.countByStatus(StatusRequest.MENUNGGU_VERIFIKASI);
        long diverifikasi = requestLayananRepository.countByStatus(StatusRequest.VERIFIKASI);
        long ditolak = requestLayananRepository.countByStatus(StatusRequest.DITOLAK);

        RequestLayananStatisticsDTO result = new RequestLayananStatisticsDTO(
            total, menungguVerifikasi, diverifikasi, ditolak
        );
        
        log.info("=== Statistics calculated: {}", result);
        return result;
    }

    /**
     * APPROVE REQUEST - klien get in with default status (BELUM)
     * Cache evicted on approval
     */
    @Transactional
    @CacheEvict(value = {"requestStatistics", "leadScoring", "leadStatistics", "requestDetails"}, allEntries = true)
    public RequestLayanan approve(Integer id, Integer userId, String role) {
        RequestLayanan request = findById(id);
        
        if (request.getStatus() == StatusRequest.VERIFIKASI) {
            throw new RuntimeException("Request sudah diverifikasi sebelumnya");
        }

        Admin admin = adminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        String approverName = admin.getNamaLengkap();

        request.setStatus(StatusRequest.VERIFIKASI);
        request.setTglVerifikasi(new Date());
        request.setKeteranganPenolakan(null);
        request.setApprovedByName(approverName);

        Klien klien = request.getKlien();
        klien.setStatus(StatusKlien.BELUM);
        klienRepository.save(klien);
        
        RequestLayanan saved = requestLayananRepository.save(request);

        String namaKlien = saved.getKlien().getNamaKlien();
        String namaLayanan = saved.getLayanan().getNamaLayanan();
        String emailKlien = saved.getKlien().getEmailKlien();
        
        notificationPublisher.publishFullNotificationWithDetails(
                "REQUEST_VERIFIED",
                "Request Berhasil Diverifikasi",
                String.format("%s meminta layanan %s. Diverifikasi oleh %s.", 
                        namaKlien, namaLayanan, approverName),
                "/admin/request-layanan/" + saved.getIdRequest(),
                emailKlien,
                namaKlien,
                namaLayanan,
                approverName
        );

        log.info("Request {} APPROVED by {} - Caches evicted, email sent", id, approverName);
        return saved;
    }

    /**
     * REJECT REQUEST - send via RabbitMQ
     * Cache evicted on rejection
     */
    @Transactional
    @CacheEvict(value = {"requestStatistics", "leadScoring", "leadStatistics", "requestDetails"}, allEntries = true)
    public RequestLayanan reject(Integer id, String keterangan) {
        RequestLayanan request = findById(id);
        
        if (request.getStatus() == StatusRequest.DITOLAK) {
            throw new RuntimeException("Request sudah ditolak sebelumnya");
        }

        request.setStatus(StatusRequest.DITOLAK);
        request.setTglVerifikasi(new Date());
        request.setKeteranganPenolakan(keterangan);

        RequestLayanan saved = requestLayananRepository.save(request);

        String namaKlien = saved.getKlien().getNamaKlien();
        String namaLayanan = saved.getLayanan().getNamaLayanan();
        String emailKlien = saved.getKlien().getEmailKlien();
        
        // send via RabbitMQ
        notificationPublisher.publishFullNotificationWithDetails(
                "REQUEST_REJECTED",
                "Request Ditolak",
                String.format("Request dari %s untuk layanan %s telah ditolak. Alasan: %s", 
                        namaKlien, namaLayanan, keterangan),
                "/admin/request-layanan/" + saved.getIdRequest(),
                emailKlien,
                namaKlien,
                namaLayanan,
                keterangan
        );

        log.info("Request {} REJECTED - Caches evicted, email sent", id);
        return saved;
    }

    public RequestLayanan save(RequestLayanan request) {
        return requestLayananRepository.save(request);
    }
}