package com.PPPL.backend.repository.project;

import com.PPPL.backend.model.enums.ProjectCategory;
import com.PPPL.backend.model.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
    // Find all active projects ordered by display order
    List<Project> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    // Find all projects for admin
    List<Project> findAllByOrderByDisplayOrderAsc();
    
    // Find featured projects
    List<Project> findByIsActiveTrueAndIsFeaturedTrueOrderByDisplayOrderAsc();
    
    // Search with filters - Paginated
    @Query("SELECT p FROM Project p WHERE p.isActive = true " +
           "AND (:searchQuery IS NULL OR " +
           "LOWER(p.projectTitle) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
           "LOWER(p.projectDescription) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
           "LOWER(p.projectClient) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
           "AND (:category IS NULL OR p.projectCategory = :category) " +
           "AND (:year IS NULL OR p.projectYear = :year) " +
           "ORDER BY p.displayOrder ASC")
    Page<Project> searchProjects(
        @Param("searchQuery") String searchQuery,
        @Param("category") ProjectCategory category,
        @Param("year") Integer year,
        Pageable pageable
    );
    
    // Get distinct years
    @Query("SELECT DISTINCT p.projectYear FROM Project p WHERE p.isActive = true AND p.projectYear IS NOT NULL ORDER BY p.projectYear DESC")
    List<Integer> findDistinctYears();
}