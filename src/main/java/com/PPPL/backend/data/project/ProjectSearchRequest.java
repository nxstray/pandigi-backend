package com.PPPL.backend.data.project;

import com.PPPL.backend.model.enums.ProjectCategory;
import lombok.Data;

@Data
public class ProjectSearchRequest {
    private String searchQuery;
    private ProjectCategory category;
    private Integer year;
    private Integer page = 0;
    private Integer size = 12;
}
