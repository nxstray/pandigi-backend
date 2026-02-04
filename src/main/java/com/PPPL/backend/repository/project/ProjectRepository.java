package com.PPPL.backend.repository.project;

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
    
    // Search with filters - Native Query (fix bytea issue)
    @Query(value = 
        "SELECT * FROM projects p " +
        "WHERE p.is_active = true " +
        "AND (:searchQuery IS NULL OR " +
        "    LOWER(p.project_title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
        "    LOWER(p.project_description) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
        "    LOWER(p.project_client) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
        "AND (:category IS NULL OR p.project_category = :category) " +
        "AND (:year IS NULL OR p.project_year = :year) " +
        "ORDER BY p.display_order ASC",
        countQuery = 
        "SELECT COUNT(*) FROM projects p " +
        "WHERE p.is_active = true " +
        "AND (:searchQuery IS NULL OR " +
        "    LOWER(p.project_title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
        "    LOWER(p.project_description) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
        "    LOWER(p.project_client) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
        "AND (:category IS NULL OR p.project_category = :category) " +
        "AND (:year IS NULL OR p.project_year = :year)",
        nativeQuery = true)
    Page<Project> searchProjects(
        @Param("searchQuery") String searchQuery,
        @Param("category") String category,
        @Param("year") Integer year,
        Pageable pageable
    );
    
    // Get distinct years
    @Query("SELECT DISTINCT p.projectYear FROM Project p WHERE p.isActive = true AND p.projectYear IS NOT NULL ORDER BY p.projectYear DESC")
    List<Integer> findDistinctYears();
}