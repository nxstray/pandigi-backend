package com.PPPL.backend.model.content;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

import com.PPPL.backend.model.admin.Admin;
import com.PPPL.backend.model.enums.ContentType;
import com.PPPL.backend.model.enums.PageName;

@Entity
@Table(name = "content_page", indexes = {
    @Index(name = "idx_page_section", columnList = "page_name,section_key"),
    @Index(name = "idx_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentPage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_content")
    private Integer idContent;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "page_name", nullable = false, length = 50)
    private PageName pageName;
    
    @Column(name = "section_key", nullable = false, length = 100)
    private String sectionKey;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;
    
    @Column(name = "content_value", columnDefinition = "TEXT")
    private String contentValue;
    
    @Column(name = "content_label", length = 200)
    private String contentLabel;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToOne
    @JoinColumn(name = "updated_by_admin_id")
    private Admin updatedBy;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
        
        // Prevent null values
        if (isActive == null) {
            isActive = true;
        }
        if (displayOrder == null) {
            displayOrder = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
        
        // Prevent null values saat update
        if (isActive == null) {
            isActive = true;
        }
        if (displayOrder == null) {
            displayOrder = 0;
        }
    }
}