package com.PPPL.backend.model.project;

import com.PPPL.backend.model.admin.Admin;
import com.PPPL.backend.model.enums.ProjectCategory;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Data
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_project")
    private Integer idProject;
    
    @Column(name = "project_title", nullable = false, length = 200)
    private String projectTitle;
    
    @Column(name = "project_description", columnDefinition = "TEXT")
    private String projectDescription;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "project_category", nullable = false, length = 50)
    private ProjectCategory projectCategory;
    
    @Column(name = "project_image", columnDefinition = "TEXT")
    private String projectImage;
    
    @Column(name = "project_client", length = 150)
    private String projectClient;
    
    @Column(name = "project_year")
    private Integer projectYear;
    
    @Column(name = "project_technologies", columnDefinition = "TEXT")
    private String projectTechnologies;
    
    @Column(name = "project_url", length = 500)
    private String projectUrl;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_admin_id")
    private Admin updatedBy;
}