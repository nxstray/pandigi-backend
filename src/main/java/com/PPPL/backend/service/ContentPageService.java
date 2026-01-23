package com.PPPL.backend.service;

import com.PPPL.backend.data.*;
import com.PPPL.backend.model.*;
import com.PPPL.backend.repository.AdminRepository;
import com.PPPL.backend.repository.ContentPageRepository;
import com.PPPL.backend.security.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentPageService {
    
    @Autowired
    private ContentPageRepository contentPageRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    /**
     * Get all active content for a specific page untuk client
     */
    public PageContentResponse getPageContent(PageName pageName) {
        List<ContentPage> contents = contentPageRepository
            .findByPageNameAndIsActiveOrderByDisplayOrder(pageName, true);
        
        Map<String, Object> contentMap = new HashMap<>();
        
        for (ContentPage content : contents) {
            String key = content.getSectionKey();
            Object value = parseContentValue(content);
            contentMap.put(key, value);
        }
        
        return new PageContentResponse(pageName.name(), contentMap);
    }
    
    /**
     * Get all content for a page including inactive untuk admin
     */
    public List<ContentPageDTO> getPageContentForAdmin(PageName pageName) {
        List<ContentPage> contents = contentPageRepository.findByPageNameOrderByDisplayOrder(pageName);
        return contents.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all content grouped by page
     */
    public Map<String, List<ContentPageDTO>> getAllContentGrouped() {
        List<ContentPage> allContent = contentPageRepository
            .findByIsActiveOrderByPageNameAscDisplayOrderAsc(true);
        
        return allContent.stream()
            .map(this::toDTO)
            .collect(Collectors.groupingBy(
                dto -> dto.getPageName().name()
            ));
    }
    
    /**
     * Update single content
     */
    @Transactional
    public ContentPageDTO updateContent(
        Integer idContent, 
        UpdateContentRequest request,
        AuthUser auth
    ) {
        ContentPage content;
        
        if (idContent != null) {
            content = contentPageRepository.findById(idContent)
                .orElseThrow(() -> new RuntimeException("Content tidak ditemukan"));
        } else {
            // Create new if not exists
            Optional<ContentPage> existing = contentPageRepository
                .findByPageNameAndSectionKey(request.getPageName(), request.getSectionKey());
            
            content = existing.orElse(new ContentPage());
            content.setPageName(request.getPageName());
            content.setSectionKey(request.getSectionKey());
            
            // Set default isActive untuk content baru
            if (content.getIsActive() == null) {
                content.setIsActive(true);
            }
        }
        
        content.setContentType(request.getContentType());
        content.setContentValue(request.getContentValue());
        content.setContentLabel(request.getContentLabel());
        content.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        
        Admin admin = adminRepository.findById(auth.userId())
            .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));
        content.setUpdatedBy(admin);
        
        ContentPage saved = contentPageRepository.save(content);
        return toDTO(saved);
    }
    
    /**
     * Bulk update content for a page
     */
    @Transactional
    public List<ContentPageDTO> bulkUpdateContent(
        PageName pageName,
        BulkUpdateContentRequest request,
        AuthUser auth
    ) {
        List<ContentPageDTO> results = new ArrayList<>();
        
        Admin admin = adminRepository.findById(auth.userId())
            .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));
        
        for (UpdateContentRequest item : request.getContents()) {
            Optional<ContentPage> existing = contentPageRepository
                .findByPageNameAndSectionKey(pageName, item.getSectionKey());
            
            ContentPage content;
            if (existing.isPresent()) {
                content = existing.get();
            } else {
                content = new ContentPage();
                content.setPageName(pageName);
                content.setSectionKey(item.getSectionKey());
            }
            
            content.setContentType(item.getContentType());
            content.setContentValue(item.getContentValue());
            content.setContentLabel(item.getContentLabel());
            content.setDisplayOrder(item.getDisplayOrder() != null ? item.getDisplayOrder() : 0);
            content.setUpdatedBy(admin);
            
            ContentPage saved = contentPageRepository.save(content);
            results.add(toDTO(saved));
        }
        
        return results;
    }
    
    /**
     * Toggle active status
     */
    @Transactional
    public ContentPageDTO toggleActive(Integer idContent) {
        ContentPage content = contentPageRepository.findById(idContent)
            .orElseThrow(() -> new RuntimeException("Content tidak ditemukan"));
        
        content.setIsActive(!content.getIsActive());
        ContentPage saved = contentPageRepository.save(content);
        return toDTO(saved);
    }
    
    /**
     * Delete content
     */
    @Transactional
    public void deleteContent(Integer idContent) {
        contentPageRepository.deleteById(idContent);
    }
    
    /**
     * Parse content value based on type
     */
    private Object parseContentValue(ContentPage content) {
        if (content.getContentValue() == null) {
            return null;
        }
        
        return switch (content.getContentType()) {
            case NUMBER -> {
                try {
                    yield Integer.parseInt(content.getContentValue());
                } catch (NumberFormatException e) {
                    yield content.getContentValue();
                }
            }
            case JSON -> content.getContentValue(); // Frontend will parse JSON
            default -> content.getContentValue();
        };
    }
    
    /**
     * DTO Converter
     */
    private ContentPageDTO toDTO(ContentPage content) {
        ContentPageDTO dto = new ContentPageDTO();
        dto.setIdContent(content.getIdContent());
        dto.setPageName(content.getPageName());
        dto.setSectionKey(content.getSectionKey());
        dto.setContentType(content.getContentType());
        dto.setContentValue(content.getContentValue());
        dto.setContentLabel(content.getContentLabel());
        dto.setDisplayOrder(content.getDisplayOrder());
        dto.setIsActive(content.getIsActive());
        dto.setUpdatedAt(content.getUpdatedAt());
        
        if (content.getUpdatedBy() != null) {
            dto.setUpdatedByName(content.getUpdatedBy().getNamaLengkap());
        }
        
        return dto;
    }
}