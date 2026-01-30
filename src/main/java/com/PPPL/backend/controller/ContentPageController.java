package com.PPPL.backend.controller;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.data.content.BulkUpdateContentRequest;
import com.PPPL.backend.data.content.ContentPageDTO;
import com.PPPL.backend.data.content.UpdateContentRequest;
import com.PPPL.backend.model.enums.PageName;
import com.PPPL.backend.security.AuthUser;
import com.PPPL.backend.service.ContentPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/content")
@CrossOrigin(origins = "http://localhost:4200")
public class ContentPageController {
    
    @Autowired
    private ContentPageService contentPageService;
    
    /**
     * Get all content grouped by page (untuk overview)
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Map<String, List<ContentPageDTO>>>> getAllContent() {
        Map<String, List<ContentPageDTO>> grouped = contentPageService.getAllContentGrouped();
        return ResponseEntity.ok(ApiResponse.success(grouped));
    }
    
    /**
     * Get content for specific page (untuk edit)
     */
    @GetMapping("/pages/{pageName}")
    public ResponseEntity<ApiResponse<List<ContentPageDTO>>> getPageContentForAdmin(
        @PathVariable PageName pageName
    ) {
        List<ContentPageDTO> content = contentPageService.getPageContentForAdmin(pageName);
        return ResponseEntity.ok(ApiResponse.success(content));
    }
    
    /**
     * Update single content
     */
    @PutMapping("/{idContent}")
    public ResponseEntity<ApiResponse<ContentPageDTO>> updateContent(
        @PathVariable Integer idContent,
        @RequestBody UpdateContentRequest request
    ) {
        AuthUser auth = AuthUser.fromContext();
        ContentPageDTO updated = contentPageService.updateContent(idContent, request, auth);
        return ResponseEntity.ok(ApiResponse.success("Content berhasil diupdate", updated));
    }
    
    /**
     * Create new content
     */
    @PostMapping("/content")
    public ResponseEntity<ApiResponse<ContentPageDTO>> createContent(
        @RequestBody UpdateContentRequest request
    ) {
        AuthUser auth = AuthUser.fromContext();
        ContentPageDTO created = contentPageService.updateContent(null, request, auth);
        return ResponseEntity.ok(ApiResponse.success("Content berhasil dibuat", created));
    }
    
    /**
     * Bulk update page content
     */
    @PutMapping("/pages/{pageName}/bulk")
    public ResponseEntity<ApiResponse<List<ContentPageDTO>>> bulkUpdateContent(
        @PathVariable PageName pageName,
        @RequestBody BulkUpdateContentRequest request
    ) {
        AuthUser auth = AuthUser.fromContext();
        List<ContentPageDTO> updated = contentPageService.bulkUpdateContent(pageName, request, auth);
        return ResponseEntity.ok(ApiResponse.success("Content berhasil diupdate", updated));
    }
    
    /**
     * Toggle active status
     */
    @PatchMapping("/{idContent}/toggle")
    public ResponseEntity<ApiResponse<ContentPageDTO>> toggleActive(
        @PathVariable Integer idContent
    ) {
        ContentPageDTO toggled = contentPageService.toggleActive(idContent);
        return ResponseEntity.ok(ApiResponse.success("Status berhasil diubah", toggled));
    }
    
    /**
     * Delete content
     */
    @DeleteMapping("/{idContent}")
    public ResponseEntity<ApiResponse<Void>> deleteContent(
        @PathVariable Integer idContent
    ) {
        contentPageService.deleteContent(idContent);
        return ResponseEntity.ok(ApiResponse.success("Content berhasil dihapus", null));
    }
}