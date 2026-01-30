package com.PPPL.backend.event;

import com.PPPL.backend.config.RabbitMQConfig;
import com.PPPL.backend.data.notification.NotificationDTO;
import com.PPPL.backend.data.notification.NotificationEventDTO;
import com.PPPL.backend.model.notification.Notification;
import com.PPPL.backend.service.EmailService;
import com.PPPL.backend.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;

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
     * Consumer utama: Terima dari RabbitMQ ke Broadcast ke WebSocket
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_WS_ADMIN)
    public void handleAdminNotification(NotificationEventDTO event) {
        log.info("RabbitMQ received: {}", event.getTitle());

        try {
            Notification saved = notificationService.createNotification(
                    event.getType(),
                    event.getTitle(),
                    event.getMessage(),
                    event.getLink()
            );

            NotificationDTO dto = new NotificationDTO(
                    saved.getIdNotification(),
                    saved.getType(),
                    saved.getTitle(),
                    saved.getMessage(),
                    saved.getLink(),
                    saved.getIsRead(),
                    saved.getCreatedAt()
            );

            messagingTemplate.convertAndSend("/topic/admin/notifications", dto);
            
            log.info("WebSocket broadcast SUCCESS: {}", saved.getTitle());
            
        } catch (Exception e) {
            log.error("Error processing admin notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Consumer untuk kirim email dengan template profesional
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL)
    public void handleEmailNotification(NotificationEventDTO event) {
        log.info("Email queue received: {} (type: {})", event.getEmail(), event.getType());

        try {
            String htmlContent;
            
            // Pilih template berdasarkan tipe notifikasi
            if ("REQUEST_VERIFIED".equals(event.getType())) {
                htmlContent = buildVerifiedEmailTemplate(event);
            } else if ("REQUEST_REJECTED".equals(event.getType())) {
                htmlContent = buildRejectedEmailTemplate(event);
            } else {
                // Fallback
                htmlContent = event.getMessage() != null ? event.getMessage() : "Notification";
            }
            
            String subject = event.getType() != null && event.getType().startsWith("REQUEST_") 
                ? (event.getType().equals("REQUEST_VERIFIED") 
                    ? "Request Layanan Diverifikasi - PT. Pandawa Digital Mandiri"
                    : "Request Layanan Ditolak - PT. Pandawa Digital Mandiri")
                : "Notifikasi - PT. Pandawa Digital Mandiri";
            
            emailService.sendEmail(event.getEmail(), subject, htmlContent);
            log.info("Email sent successfully to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
        }
    }
    
    /**
     * TEMPLATE EMAIL: Request Verified
     */
    private String buildVerifiedEmailTemplate(NotificationEventDTO event) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.8;
                        color: #2d3748;
                        max-width: 650px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f7fafc;
                    }
                    .email-container {
                        background: linear-gradient(135deg, #0C2B40 0%%, #1F4E79 100%%);
                        padding: 2px;
                        border-radius: 12px;
                    }
                    .email-content {
                        background: white;
                        padding: 45px 40px;
                        border-radius: 11px;
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 35px;
                        padding-bottom: 25px;
                        border-bottom: 2px solid #e2e8f0;
                    }
                    .header h1 {
                        color: #0C2B40;
                        margin: 0;
                        font-size: 26px;
                        font-weight: 600;
                        letter-spacing: -0.5px;
                    }
                    .greeting {
                        font-size: 20px;
                        color: #1a202c;
                        margin-bottom: 25px;
                        font-weight: 500;
                    }
                    .section-title {
                        font-size: 16px;
                        color: #0C2B40;
                        font-weight: 600;
                        margin: 30px 0 15px 0;
                        letter-spacing: 0.3px;
                    }
                    .content-text {
                        font-size: 15px;
                        line-height: 1.8;
                        color: #4a5568;
                        margin-bottom: 15px;
                        text-align: justify;
                    }
                    .info-detail {
                        background: #f7fafc;
                        padding: 20px 25px;
                        border-left: 4px solid #0C2B40;
                        border-radius: 6px;
                        margin: 25px 0;
                    }
                    .info-row {
                        margin: 12px 0;
                        font-size: 15px;
                    }
                    .info-label {
                        color: #718096;
                        display: inline-block;
                        width: 160px;
                        font-weight: 500;
                    }
                    .info-value {
                        color: #2d3748;
                        font-weight: 600;
                    }
                    .divider {
                        height: 1px;
                        background: #e2e8f0;
                        margin: 30px 0;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 40px;
                        padding-top: 25px;
                        border-top: 1px solid #e2e8f0;
                    }
                    .footer-text {
                        color: #718096;
                        font-size: 14px;
                        line-height: 1.6;
                        margin: 8px 0;
                    }
                    .copyright {
                        color: #a0aec0;
                        font-size: 12px;
                        margin-top: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="email-content">
                        <div class="header">
                            <h1>PT. Pandawa Digital Mandiri</h1>
                        </div>
                        
                        <div class="greeting">Kepada Yth. %s,</div>
                        
                        <p class="content-text">
                            Kami dengan senang hati menginformasikan bahwa request layanan Anda telah melalui proses verifikasi dan telah disetujui oleh tim kami. 
                            Setelah melakukan evaluasi menyeluruh terhadap proposal yang Anda ajukan, termasuk analisis terhadap estimasi biaya, timeline pengerjaan, 
                            serta kebutuhan teknis yang disampaikan, kami menyimpulkan bahwa request Anda memenuhi kriteria dan standar kualitas yang kami tetapkan.
                        </p>
                        
                        <div class="info-detail">
                            <div class="info-row">
                                <span class="info-label">Layanan yang Diminta</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Status Verifikasi</span>
                                <span class="info-value">Disetujui</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Diverifikasi oleh</span>
                                <span class="info-value">%s</span>
                            </div>
                        </div>
                        
                        <div class="section-title">Langkah Selanjutnya</div>
                        <p class="content-text">
                            Tim teknis kami akan segera menghubungi Anda melalui email atau telepon dalam 1-2 hari kerja untuk membahas detail teknis pelaksanaan proyek, 
                            termasuk jadwal kick-off meeting, timeline detail, serta pembahasan kontrak kerja sama. Kami berkomitmen untuk memberikan layanan terbaik 
                            dan memastikan proyek Anda berjalan sesuai ekspektasi.
                        </p>
                        
                        <p class="content-text">
                            Apabila Anda memiliki pertanyaan lebih lanjut atau memerlukan klarifikasi tambahan, jangan ragu untuk menghubungi tim customer support kami. 
                            Kami siap membantu Anda dengan senang hati.
                        </p>
                        
                        <div class="divider"></div>
                        
                        <div class="footer">
                            <p class="footer-text">
                                Email ini dikirim secara otomatis oleh sistem.<br>
                                Untuk informasi lebih lanjut, silakan hubungi tim support kami.
                            </p>
                            <p class="footer-text" style="margin-top: 15px; color: #4a5568;">
                                <strong>PT. Pandawa Digital Mandiri</strong><br>
                                Email: support@pandawadigital.com | Telepon: (021) 1234-5678
                            </p>
                            <p class="copyright">
                                © 2025 PT. Pandawa Digital Mandiri. All rights reserved.
                            </p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            event.getNamaKlien(),
            event.getNamaLayanan(),
            event.getKeterangan()
        );
    }
    
    /**
     * TEMPLATE EMAIL: Request Rejected
     */
    private String buildRejectedEmailTemplate(NotificationEventDTO event) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.8;
                        color: #2d3748;
                        max-width: 650px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f7fafc;
                    }
                    .email-container {
                        background: linear-gradient(135deg, #0C2B40 0%%, #1F4E79 100%%);
                        padding: 2px;
                        border-radius: 12px;
                    }
                    .email-content {
                        background: white;
                        padding: 45px 40px;
                        border-radius: 11px;
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 35px;
                        padding-bottom: 25px;
                        border-bottom: 2px solid #e2e8f0;
                    }
                    .header h1 {
                        color: #0C2B40;
                        margin: 0;
                        font-size: 26px;
                        font-weight: 600;
                        letter-spacing: -0.5px;
                    }
                    .greeting {
                        font-size: 20px;
                        color: #1a202c;
                        margin-bottom: 25px;
                        font-weight: 500;
                    }
                    .section-title {
                        font-size: 16px;
                        color: #0C2B40;
                        font-weight: 600;
                        margin: 30px 0 15px 0;
                        letter-spacing: 0.3px;
                    }
                    .content-text {
                        font-size: 15px;
                        line-height: 1.8;
                        color: #4a5568;
                        margin-bottom: 15px;
                        text-align: justify;
                    }
                    .info-detail {
                        background: #f7fafc;
                        padding: 20px 25px;
                        border-left: 4px solid #0C2B40;
                        border-radius: 6px;
                        margin: 25px 0;
                    }
                    .info-row {
                        margin: 12px 0;
                        font-size: 15px;
                    }
                    .info-label {
                        color: #718096;
                        display: inline-block;
                        width: 160px;
                        font-weight: 500;
                    }
                    .info-value {
                        color: #2d3748;
                        font-weight: 600;
                    }
                    .reason-box {
                        background: #fff5f5;
                        border-left: 4px solid #742a2a;
                        padding: 20px 25px;
                        border-radius: 6px;
                        margin: 25px 0;
                    }
                    .reason-text {
                        color: #742a2a;
                        font-size: 15px;
                        line-height: 1.8;
                        margin: 0;
                    }
                    .divider {
                        height: 1px;
                        background: #e2e8f0;
                        margin: 30px 0;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 40px;
                        padding-top: 25px;
                        border-top: 1px solid #e2e8f0;
                    }
                    .footer-text {
                        color: #718096;
                        font-size: 14px;
                        line-height: 1.6;
                        margin: 8px 0;
                    }
                    .copyright {
                        color: #a0aec0;
                        font-size: 12px;
                        margin-top: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="email-content">
                        <div class="header">
                            <h1>PT. Pandawa Digital Mandiri</h1>
                        </div>
                        
                        <div class="greeting">Kepada Yth. %s,</div>
                        
                        <p class="content-text">
                            Terima kasih atas kepercayaan Anda yang telah mengajukan request layanan kepada PT. Pandawa Digital Mandiri. 
                            Setelah melakukan evaluasi menyeluruh terhadap proposal yang Anda ajukan, termasuk analisis mendalam terhadap aspek teknis, 
                            estimasi biaya, timeline pengerjaan, serta kesesuaian dengan kapasitas dan standar operasional kami saat ini, 
                            dengan berat hati kami informasikan bahwa request layanan Anda belum dapat kami proses pada tahap ini.
                        </p>
                        
                        <div class="info-detail">
                            <div class="info-row">
                                <span class="info-label">Layanan yang Diminta</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Status Verifikasi</span>
                                <span class="info-value">Tidak Disetujui</span>
                            </div>
                        </div>
                        
                        <div class="section-title">Alasan Penolakan</div>
                        <div class="reason-box">
                            <p class="reason-text">%s</p>
                        </div>
                        
                        <p class="content-text">
                            Kami memahami bahwa keputusan ini mungkin mengecewakan, namun kami berkomitmen untuk tetap transparan dalam setiap proses bisnis kami. 
                            Keputusan ini diambil setelah pertimbangan matang dari tim evaluasi kami dengan tujuan memastikan kualitas layanan yang optimal 
                            bagi seluruh klien kami.
                        </p>
                        
                        <div class="section-title">Saran dan Tindak Lanjut</div>
                        <p class="content-text">
                            Apabila Anda berkenan untuk melakukan revisi terhadap proposal yang telah diajukan, kami dengan senang hati akan mempertimbangkan 
                            kembali request Anda. Anda dapat menghubungi tim customer support kami untuk mendiskusikan kemungkinan penyesuaian yang dapat dilakukan, 
                            baik dari segi scope pekerjaan, timeline, maupun aspek lainnya yang relevan.
                        </p>
                        
                        <p class="content-text">
                            Kami sangat menghargai minat Anda untuk bekerja sama dengan PT. Pandawa Digital Mandiri dan berharap dapat melayani Anda di kesempatan mendatang. 
                            Jangan ragu untuk menghubungi kami jika Anda memiliki pertanyaan atau memerlukan klarifikasi lebih lanjut.
                        </p>
                        
                        <div class="divider"></div>
                        
                        <div class="footer">
                            <p class="footer-text">
                                Email ini dikirim secara otomatis oleh sistem.<br>
                                Untuk informasi lebih lanjut, silakan hubungi tim support kami.
                            </p>
                            <p class="footer-text" style="margin-top: 15px; color: #4a5568;">
                                <strong>PT. Pandawa Digital Mandiri</strong><br>
                                Email: support@pandawadigital.com | Telepon: (021) 1234-5678
                            </p>
                            <p class="copyright">
                                © 2025 PT. Pandawa Digital Mandiri. All rights reserved.
                            </p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            event.getNamaKlien(),
            event.getNamaLayanan(),
            event.getKeterangan()
        );
    }
}