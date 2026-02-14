package com.PPPL.backend.data.project;

import com.PPPL.backend.model.enums.ProjectCategory;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProjectRequest {
    @Size(min = 3, max = 200, message = "Judul project harus antara 3-200 karakter")
    private String projectTitle;

    @Size(min = 10, max = 2000, message = "Deskripsi harus antara 10-2000 karakter")
    private String projectDescription;
    
    private ProjectCategory projectCategory;
    private String projectImage;

    @Size(max = 100, message = "Nama klien maksimal 100 karakter")
    private String projectClient;

    @Min(value = 2000, message = "Tahun project tidak valid")
    @Max(value = 2100, message = "Tahun project tidak valid")
    private Integer projectYear;
    
    // Optional - can be null or empty list
    private List<String> projectTechnologies;
    
    @Size(max = 2048, message = "URL project maksimal 2048 karakter")
    private String projectUrl;

    private Boolean isFeatured;
    private Integer displayOrder;
}