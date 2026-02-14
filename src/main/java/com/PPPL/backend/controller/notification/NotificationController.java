package com.PPPL.backend.controller.notification;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.data.notification.NotificationDTO;
import com.PPPL.backend.event.NotificationEventPublisher;
import com.PPPL.backend.service.email.EmailService;
import com.PPPL.backend.service.notification.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationEventPublisher notificationPublisher;
    
    /**
     * Test real-time notification via RabbitMQ + WebSocket
     */
    @PostMapping("/test-realtime")
    public ResponseEntity<ApiResponse<String>> testRealtimeNotification() {
        try {
            notificationPublisher.publishAdminNotification(
                    "TEST",
                    "Test Notifikasi Real-time",
                    "Ini adalah test notifikasi real-time via RabbitMQ + WebSocket",
                    "/admin/dashboard"
            );
            return ResponseEntity.ok(ApiResponse.success("Notifikasi real-time berhasil dikirim! Cek dashboard Anda."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal kirim notifikasi: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getAllNotifications() {
        try {
            List<NotificationDTO> notifications = notificationService.getAllNotifications();
            return ResponseEntity.ok(ApiResponse.success(notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat notifikasi: " + e.getMessage()));
        }
    }
    
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getRecentNotifications() {
        try {
            List<NotificationDTO> notifications = notificationService.getRecentNotifications(10);
            return ResponseEntity.ok(ApiResponse.success(notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat notifikasi: " + e.getMessage()));
        }
    }
    
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotifications() {
        try {
            List<NotificationDTO> notifications = notificationService.getUnreadNotifications();
            return ResponseEntity.ok(ApiResponse.success(notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat notifikasi: " + e.getMessage()));
        }
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount() {
        try {
            long count = notificationService.getUnreadCount();
            return ResponseEntity.ok(ApiResponse.success(Map.of("count", count)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal menghitung notifikasi: " + e.getMessage()));
        }
    }
    
    @PostMapping("/test-email")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> testEmail(@RequestParam String to) {
        try {
            emailService.sendEmail(
                to,
                "TEST EMAIL SMTP",
                "<h3>SMTP berhasil!</h3><p>Email ini dikirim dari backend</p>"
            );
            return ResponseEntity.ok(ApiResponse.success("Email berhasil dikirim"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal kirim email: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Integer id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok(ApiResponse.success("Notifikasi ditandai sudah dibaca"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal update notifikasi: " + e.getMessage()));
        }
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead() {
        try {
            notificationService.markAllAsRead();
            return ResponseEntity.ok(ApiResponse.success("Semua notifikasi ditandai sudah dibaca"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal update notifikasi: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable Integer id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok(ApiResponse.success("Notifikasi berhasil dihapus"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal hapus notifikasi: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/cleanup")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> cleanupOldNotifications() {
        try {
            notificationService.deleteOldNotifications();
            return ResponseEntity.ok(ApiResponse.success("Notifikasi lama berhasil dibersihkan"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal cleanup: " + e.getMessage()));
        }
    }
}