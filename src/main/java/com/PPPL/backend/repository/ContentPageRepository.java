package com.PPPL.backend.repository;

import com.PPPL.backend.model.ContentPage;
import com.PPPL.backend.model.PageName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContentPageRepository extends JpaRepository<ContentPage, Integer> {
    
    List<ContentPage> findByPageNameAndIsActiveOrderByDisplayOrder(PageName pageName, Boolean isActive);
    
    List<ContentPage> findByPageNameOrderByDisplayOrder(PageName pageName);
    
    Optional<ContentPage> findByPageNameAndSectionKey(PageName pageName, String sectionKey);
    
    List<ContentPage> findByIsActiveOrderByPageNameAscDisplayOrderAsc(Boolean isActive);
    
    long countByPageName(PageName pageName);
}