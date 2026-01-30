package com.PPPL.backend.repository.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.PPPL.backend.model.notification.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    // Cari semua notifikasi yang belum dibaca
    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();
    
    // Cari semua notifikasi sesuai urutan waktu terbaru
    List<Notification> findAllByOrderByCreatedAtDesc();
    
    // Hitung jumlah notifikasi yang belum dibaca
    long countByIsReadFalse();
    
    // Cari notifikasi berdasarkan tipe
    List<Notification> findByTypeOrderByCreatedAtDesc(String type);
    
    // Cari notifikasi terbaru dengan batasan jumlah
    @Query(value = "SELECT * FROM notifications ORDER BY created_at DESC LIMIT ?1", nativeQuery = true)
    List<Notification> findRecentNotifications(int limit);
}