package com.PPPL.backend.service.client;

import com.PPPL.backend.data.client.ClientFormDTO;
import com.PPPL.backend.event.NotificationEventPublisher;
import com.PPPL.backend.handler.ResourceNotFoundException;
import com.PPPL.backend.model.admin.Klien;
import com.PPPL.backend.model.enums.StatusKlien;
import com.PPPL.backend.model.enums.StatusRequest;
import com.PPPL.backend.model.layanan.Layanan;
import com.PPPL.backend.model.layanan.RequestLayanan;
import com.PPPL.backend.repository.client.KlienRepository;
import com.PPPL.backend.repository.layanan.LayananRepository;
import com.PPPL.backend.repository.layanan.RequestLayananRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class ClientFormService {

    @Autowired
    private KlienRepository klienRepository;

    @Autowired
    private LayananRepository layananRepository;

    @Autowired
    private RequestLayananRepository requestLayananRepository;

    @Autowired
    private NotificationEventPublisher notificationPublisher;

    /**
     * Submit form dari client - auto create Klien + RequestLayanan
     */
    @Transactional
    public ClientFormResult submitForm(ClientFormDTO form) {
        // 1. Check if Klien with email already exists
        Klien klien = klienRepository.findByEmailKlien(form.getEmail()).orElse(null);

        // 2. Build new Klien if not exist
        boolean isNewClient = false;
        if (klien == null) {
            klien = new Klien();
            klien.setNamaKlien(form.getFullName());
            klien.setEmailKlien(form.getEmail().trim().toLowerCase());
            klien.setNoTelp(form.getPhoneNumber());
            klien.setStatus(StatusKlien.BELUM);
            klien.setTglRequest(new Date());
            klien = klienRepository.save(klien);
            isNewClient = true;

            log.info("New client created: {}", klien.getNamaKlien());
        }

        // 3. Search Layanan by ID
        Layanan layanan = layananRepository.findById(form.getIdLayanan())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Layanan dengan ID " + form.getIdLayanan() + " tidak ditemukan"));

        // 4. Build new RequestLayanan
        RequestLayanan request = new RequestLayanan();
        request.setKlien(klien);
        request.setLayanan(layanan);
        request.setTglRequest(new Date());
        request.setStatus(StatusRequest.MENUNGGU_VERIFIKASI);
        request.setPerusahaan(form.getPerusahaan());
        request.setTopic(layanan.getNamaLayanan());
        request.setPesan(form.getMessage());
        request.setAnggaran(form.getAnggaran());
        request.setWaktuImplementasi(form.getWaktuImplementasi());
        request.setAiAnalyzed(false);

        RequestLayanan savedRequest = requestLayananRepository.save(request);

        log.info("Request layanan created: id={}, layanan={}",
            savedRequest.getIdRequest(), layanan.getNamaLayanan());

        // 5. Publish notification realtime via RabbitMQ
        if (isNewClient) {
            notificationPublisher.publishAdminNotification(
                "NEW_CLIENT",
                "Klien Baru: " + klien.getNamaKlien(),
                "Klien baru telah mendaftar untuk layanan " + layanan.getNamaLayanan(),
                "/admin/klien"
            );
            log.info("Published NEW_CLIENT notification");
        }

        notificationPublisher.publishAdminNotification(
            "PENDING_VERIFICATION",
            "Request Perlu Verifikasi",
            klien.getNamaKlien() + " meminta layanan " + layanan.getNamaLayanan() +
                ". Menunggu verifikasi.",
            "/admin/request-layanan/" + savedRequest.getIdRequest()
        );
        log.info("Published PENDING_VERIFICATION notification");

        return new ClientFormResult(
            savedRequest.getIdRequest(),
            klien.getNamaKlien(),
            layanan.getNamaLayanan(),
            isNewClient
        );
    }

    /**
     * Result object from submitForm
     */
    public static class ClientFormResult {
        public final Integer idRequest;
        public final String clientName;
        public final String serviceName;
        public final boolean isNewClient;

        public ClientFormResult(Integer idRequest, String clientName,
                String serviceName, boolean isNewClient) {
            this.idRequest = idRequest;
            this.clientName = clientName;
            this.serviceName = serviceName;
            this.isNewClient = isNewClient;
        }
    }
}