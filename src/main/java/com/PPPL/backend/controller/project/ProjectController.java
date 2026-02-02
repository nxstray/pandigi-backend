package com.PPPL.backend.controller.project;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.data.project.*;
import com.PPPL.backend.security.AuthUser;
import com.PPPL.backend.service.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllProjectsForAdmin() {
        try {
            List<ProjectDTO> projects = projectService.getAllProjectsForAdmin();
            return ResponseEntity.ok(ApiResponse.success(projects));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal memuat projects: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectById(@PathVariable Integer id) {
        try {
            ProjectDTO project = projectService.getProjectById(id);
            return ResponseEntity.ok(ApiResponse.success(project));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Project tidak ditemukan: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(
        @RequestBody CreateProjectRequest request,
        @AuthenticationPrincipal AuthUser auth
    ) {
        try {
            ProjectDTO created = projectService.createProject(request, auth);
            return ResponseEntity.ok(ApiResponse.success("Project berhasil dibuat", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal membuat project: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
        @PathVariable Integer id,
        @RequestBody UpdateProjectRequest request,
        @AuthenticationPrincipal AuthUser auth
    ) {
        try {
            ProjectDTO updated = projectService.updateProject(id, request, auth);
            return ResponseEntity.ok(ApiResponse.success("Project berhasil diupdate", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal mengupdate project: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<ProjectDTO>> toggleActive(@PathVariable Integer id) {
        try {
            ProjectDTO updated = projectService.toggleActive(id);
            return ResponseEntity.ok(ApiResponse.success("Status project berhasil diubah", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal mengubah status: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/toggle-featured")
    public ResponseEntity<ApiResponse<ProjectDTO>> toggleFeatured(@PathVariable Integer id) {
        try {
            ProjectDTO updated = projectService.toggleFeatured(id);
            return ResponseEntity.ok(ApiResponse.success("Status featured berhasil diubah", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal mengubah status featured: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Integer id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok(ApiResponse.success("Project berhasil dihapus", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Gagal menghapus project: " + e.getMessage()));
        }
    }
}