package com.PPPL.backend.service.project;

import com.PPPL.backend.data.project.ProjectDTO;
import com.PPPL.backend.data.project.CreateProjectRequest;
import com.PPPL.backend.data.project.UpdateProjectRequest;
import com.PPPL.backend.data.project.ProjectSearchRequest;
import com.PPPL.backend.model.admin.Admin;
import com.PPPL.backend.model.enums.ProjectCategory;
import com.PPPL.backend.model.project.Project;
import com.PPPL.backend.repository.admin.AdminRepository;
import com.PPPL.backend.repository.project.ProjectRepository;
import com.PPPL.backend.security.AuthUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    /**
     * Get all active projects for public view
     */
    public List<ProjectDTO> getAllActiveProjects() {
        List<Project> projects = projectRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        return projects.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get featured projects
     */
    public List<ProjectDTO> getFeaturedProjects() {
        List<Project> projects = projectRepository.findByIsActiveTrueAndIsFeaturedTrueOrderByDisplayOrderAsc();
        return projects.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Search projects with filters and pagination
     */
    public Map<String, Object> searchProjects(ProjectSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        
        Page<Project> projectPage = projectRepository.searchProjects(
            request.getSearchQuery(),
            request.getCategory() != null ? request.getCategory().name() : null,
            request.getYear(),
            pageable
        );
        
        List<ProjectDTO> projects = projectPage.getContent().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("projects", projects);
        response.put("currentPage", projectPage.getNumber());
        response.put("totalPages", projectPage.getTotalPages());
        response.put("totalItems", projectPage.getTotalElements());
        response.put("hasNext", projectPage.hasNext());
        response.put("hasPrevious", projectPage.hasPrevious());
        
        return response;
    }
    
    /**
     * Get filter options (categories and years)
     */
    public Map<String, Object> getFilterOptions() {
        Map<String, Object> options = new HashMap<>();
        
        // Categories
        List<Map<String, String>> categories = new ArrayList<>();
        for (ProjectCategory category : ProjectCategory.values()) {
            Map<String, String> catMap = new HashMap<>();
            catMap.put("value", category.name());
            catMap.put("label", category.getDisplayName());
            categories.add(catMap);
        }
        options.put("categories", categories);
        
        // Years
        List<Integer> years = projectRepository.findDistinctYears();
        options.put("years", years);
        
        return options;
    }
    
    /**
     * Get all projects for admin (including inactive)
     */
    public List<ProjectDTO> getAllProjectsForAdmin() {
        List<Project> projects = projectRepository.findAllByOrderByDisplayOrderAsc();
        return projects.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get single project by ID
     */
    public ProjectDTO getProjectById(Integer idProject) {
        Project project = projectRepository.findById(idProject)
            .orElseThrow(() -> new RuntimeException("Project tidak ditemukan"));
        return toDTO(project);
    }
    
    /**
     * Create new project
     */
    @Transactional
    public ProjectDTO createProject(CreateProjectRequest request, AuthUser auth) {
        Project project = new Project();
        mapRequestToEntity(request, project);
        
        Admin admin = adminRepository.findById(auth.userId())
            .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));
        project.setUpdatedBy(admin);
        
        Project saved = projectRepository.save(project);
        return toDTO(saved);
    }
    
    /**
     * Update existing project
     */
    @Transactional
    public ProjectDTO updateProject(Integer idProject, UpdateProjectRequest request, AuthUser auth) {
        Project project = projectRepository.findById(idProject)
            .orElseThrow(() -> new RuntimeException("Project tidak ditemukan"));
        
        mapRequestToEntity(request, project);
        
        Admin admin = adminRepository.findById(auth.userId())
            .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));
        project.setUpdatedBy(admin);
        
        Project saved = projectRepository.save(project);
        return toDTO(saved);
    }
    
    /**
     * Toggle active status
     */
    @Transactional
    public ProjectDTO toggleActive(Integer idProject) {
        Project project = projectRepository.findById(idProject)
            .orElseThrow(() -> new RuntimeException("Project tidak ditemukan"));
        
        project.setIsActive(!project.getIsActive());
        Project saved = projectRepository.save(project);
        return toDTO(saved);
    }
    
    /**
     * Toggle featured status
     */
    @Transactional
    public ProjectDTO toggleFeatured(Integer idProject) {
        Project project = projectRepository.findById(idProject)
            .orElseThrow(() -> new RuntimeException("Project tidak ditemukan"));
        
        project.setIsFeatured(!project.getIsFeatured());
        Project saved = projectRepository.save(project);
        return toDTO(saved);
    }
    
    /**
     * Delete project
     */
    @Transactional
    public void deleteProject(Integer idProject) {
        projectRepository.deleteById(idProject);
    }
    
    /**
     * Map request to entity (for create/update)
     */
    private void mapRequestToEntity(Object request, Project project) {
        if (request instanceof CreateProjectRequest createReq) {
            project.setProjectTitle(createReq.getProjectTitle());
            project.setProjectDescription(createReq.getProjectDescription());
            project.setProjectCategory(createReq.getProjectCategory());
            project.setProjectImage(createReq.getProjectImage());
            project.setProjectClient(createReq.getProjectClient());
            project.setProjectYear(createReq.getProjectYear());
            project.setProjectUrl(createReq.getProjectUrl());
            project.setIsFeatured(createReq.getIsFeatured() != null ? createReq.getIsFeatured() : false);
            project.setDisplayOrder(createReq.getDisplayOrder() != null ? createReq.getDisplayOrder() : 0);
            
            // Convert List<String> to JSON string
            project.setProjectTechnologies(convertTechnologiesToJson(createReq.getProjectTechnologies()));
            
        } else if (request instanceof UpdateProjectRequest updateReq) {
            project.setProjectTitle(updateReq.getProjectTitle());
            project.setProjectDescription(updateReq.getProjectDescription());
            project.setProjectCategory(updateReq.getProjectCategory());
            project.setProjectImage(updateReq.getProjectImage());
            project.setProjectClient(updateReq.getProjectClient());
            project.setProjectYear(updateReq.getProjectYear());
            project.setProjectUrl(updateReq.getProjectUrl());
            project.setIsFeatured(updateReq.getIsFeatured() != null ? updateReq.getIsFeatured() : false);
            project.setDisplayOrder(updateReq.getDisplayOrder() != null ? updateReq.getDisplayOrder() : 0);
            
            // Convert List<String> to JSON string
            project.setProjectTechnologies(convertTechnologiesToJson(updateReq.getProjectTechnologies()));
        }
    }

    /**
     * Helper method: Convert List<String> to JSON string
     */
    private String convertTechnologiesToJson(List<String> technologies) {
        if (technologies == null || technologies.isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(technologies);
        } catch (Exception e) {
            System.err.println("Failed to convert technologies to JSON: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Convert entity to DTO
     */
    private ProjectDTO toDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setIdProject(project.getIdProject());
        dto.setProjectTitle(project.getProjectTitle());
        dto.setProjectDescription(project.getProjectDescription());
        dto.setProjectCategory(project.getProjectCategory());
        dto.setProjectImage(project.getProjectImage());
        dto.setProjectClient(project.getProjectClient());
        dto.setProjectYear(project.getProjectYear());
        dto.setProjectUrl(project.getProjectUrl());
        dto.setIsFeatured(project.getIsFeatured());
        dto.setDisplayOrder(project.getDisplayOrder());
        dto.setIsActive(project.getIsActive());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        
        // Parse JSON technologies string to List
        List<String> technologies = new ArrayList<>();
        if (project.getProjectTechnologies() != null && !project.getProjectTechnologies().trim().isEmpty()) {
            try {
                technologies = objectMapper.readValue(
                    project.getProjectTechnologies(),
                    new TypeReference<List<String>>() {}
                );
            } catch (Exception e) {
                // If JSON parsing fails, log and keep empty list
                System.err.println("Failed to parse project technologies for project " + project.getIdProject() + ": " + e.getMessage());
            }
        }
        dto.setProjectTechnologies(technologies);
        
        // Set updated by name
        if (project.getUpdatedBy() != null) {
            dto.setUpdatedByName(project.getUpdatedBy().getNamaLengkap());
        }
        
        return dto;
    }
}