package com.PPPL.backend.controller.project;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.data.project.*;
import com.PPPL.backend.service.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/projects")
public class PublicProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllActiveProjects() {
        try {
            List<ProjectDTO> projects = projectService.getAllActiveProjects();
            return ResponseEntity.ok(ApiResponse.success(projects));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat projects: " + e.getMessage()));
        }
    }
    
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getFeaturedProjects() {
        try {
            List<ProjectDTO> projects = projectService.getFeaturedProjects();
            return ResponseEntity.ok(ApiResponse.success(projects));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat featured projects: " + e.getMessage()));
        }
    }
    
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchProjects(@RequestBody ProjectSearchRequest request) {
        try {
            Map<String, Object> result = projectService.searchProjects(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal mencari projects: " + e.getMessage()));
        }
    }
    
    @GetMapping("/filter-options")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFilterOptions() {
        try {
            Map<String, Object> options = projectService.getFilterOptions();
            return ResponseEntity.ok(ApiResponse.success(options));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat filter options: " + e.getMessage()));
        }
    }
}