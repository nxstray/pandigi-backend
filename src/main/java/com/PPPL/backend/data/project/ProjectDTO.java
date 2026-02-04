package com.PPPL.backend.data.project;

import com.PPPL.backend.model.enums.ProjectCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDTO {
    private Integer idProject;
    private String projectTitle;
    private String projectDescription;
    private ProjectCategory projectCategory;
    private String projectImage;
    private String projectClient;
    private Integer projectYear;
    
    // Optional field - will default to empty list if null
    private List<String> projectTechnologies;
    
    private String projectUrl;
    private Boolean isFeatured;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedByName;
}