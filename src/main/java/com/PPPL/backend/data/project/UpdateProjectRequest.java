package com.PPPL.backend.data.project;

import com.PPPL.backend.model.enums.ProjectCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProjectRequest {
    private String projectTitle;
    private String projectDescription;
    private ProjectCategory projectCategory;
    private String projectImage;
    private String projectClient;
    private Integer projectYear;
    
    // Optional - can be null or empty list
    private List<String> projectTechnologies;
    
    private String projectUrl;
    private Boolean isFeatured;
    private Integer displayOrder;
}
