package com.PPPL.backend.event;

import com.PPPL.backend.config.RabbitMQConfig;
import com.PPPL.backend.data.NotificationDTO;
import com.PPPL.backend.data.NotificationEventDTO;
import com.PPPL.backend.model.Notification;
import com.PPPL.backend.service.EmailService;
import com.PPPL.backend.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationEventConsumer(
            NotificationService notificationService,
            EmailService emailService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * CONSUMER UTAMA: Terima dari RabbitMQ -> Broadcast ke WebSocket
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_WS_ADMIN)
    public void handleAdminNotification(NotificationEventDTO event) {
        log.info("RabbitMQ received: {}", event.getTitle());

        try {
            // 1. Simpan ke database
            Notification saved = notificationService.createNotification(
                    event.getType(),
                    event.getTitle(),
                    event.getMessage(),
                    event.getLink()
            );

            // 2. Convert to DTO
            NotificationDTO dto = new NotificationDTO(
                    saved.getIdNotification(),
                    saved.getType(),
                    saved.getTitle(),
                    saved.getMessage(),
                    saved.getLink(),
                    saved.getIsRead(),
                    saved.getCreatedAt()
            );

            // 3. BROADCAST VIA WEBSOCKET
            messagingTemplate.convertAndSend("/topic/admin/notifications", dto);
            
            log.info("WebSocket broadcast SUCCESS: {} to /topic/admin/notifications", saved.getTitle());
            
        } catch (Exception e) {
            log.error("Error processing admin notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Consumer untuk kirim email
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL)
    public void handleEmailNotification(NotificationEventDTO event) {
        log.info("Email queue received: {}", event.getTitle());

        try {
            emailService.sendEmail(
                    event.getEmail(),
                    event.getTitle(),
                    event.getMessage()
            );
            log.info("Email sent successfully to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
        }
    }
}