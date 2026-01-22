package com.PPPL.backend.event;

import com.PPPL.backend.config.RabbitMQConfig;
import com.PPPL.backend.data.NotificationEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public NotificationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publish notifikasi untuk admin (via WebSocket)
     */
    public void publishAdminNotification(
            String type,
            String title,
            String message,
            String link
    ) {
        NotificationEventDTO event = new NotificationEventDTO();
        event.setType(type);
        event.setTitle(title);
        event.setMessage(message);
        event.setLink(link);
        event.setBroadcastAdmin(true);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NOTIFICATION,
                    RabbitMQConfig.ROUTING_KEY_ADMIN,
                    event
            );
            log.info("Published to RabbitMQ (ADMIN): {}", title);
        } catch (Exception e) {
            log.error("Failed to publish admin notification: {}", e.getMessage());
        }
    }

    /**
     * Publish email untuk Request Verified dengan data lengkap
     */
    public void publishRequestVerifiedEmail(
            String email,
            String namaKlien,
            String namaLayanan,
            String approverName
    ) {
        NotificationEventDTO event = new NotificationEventDTO();
        event.setType("REQUEST_VERIFIED");
        event.setEmail(email);
        event.setNamaKlien(namaKlien);
        event.setNamaLayanan(namaLayanan);
        event.setKeterangan(approverName);
        event.setSendEmail(true);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NOTIFICATION,
                    RabbitMQConfig.ROUTING_KEY_EMAIL,
                    event
            );
            log.info("Published REQUEST_VERIFIED email to: {}", email);
        } catch (Exception e) {
            log.error("Failed to publish verified email: {}", e.getMessage());
        }
    }

    /**
     * Publish email untuk Request Rejected dengan data lengkap
     */
    public void publishRequestRejectedEmail(
            String email,
            String namaKlien,
            String namaLayanan,
            String keterangan
    ) {
        NotificationEventDTO event = new NotificationEventDTO();
        event.setType("REQUEST_REJECTED");
        event.setEmail(email);
        event.setNamaKlien(namaKlien);
        event.setNamaLayanan(namaLayanan);
        event.setKeterangan(keterangan);
        event.setSendEmail(true);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NOTIFICATION,
                    RabbitMQConfig.ROUTING_KEY_EMAIL,
                    event
            );
            log.info("Published REQUEST_REJECTED email to: {}", email);
        } catch (Exception e) {
            log.error("Failed to publish rejected email: {}", e.getMessage());
        }
    }

    /**
     * Publish dengan data lengkap untuk template profesional
     */
    public void publishFullNotificationWithDetails(
            String type,
            String title,
            String message,
            String link,
            String email,
            String namaKlien,
            String namaLayanan,
            String keterangan
    ) {
        // 1. Admin notification (WebSocket)
        publishAdminNotification(type, title, message, link);

        // 2. Email notification dengan template profesional
        if (email != null && !email.isEmpty()) {
            if ("REQUEST_VERIFIED".equals(type)) {
                publishRequestVerifiedEmail(email, namaKlien, namaLayanan, keterangan);
            } else if ("REQUEST_REJECTED".equals(type)) {
                publishRequestRejectedEmail(email, namaKlien, namaLayanan, keterangan);
            }
        }
    }
}