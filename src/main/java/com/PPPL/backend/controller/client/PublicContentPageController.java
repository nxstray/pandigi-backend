package com.PPPL.backend.controller.client;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.data.content.PageContentResponse;
import com.PPPL.backend.model.enums.PageName;
import com.PPPL.backend.service.content.ContentPageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/content")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicContentPageController {

    @Autowired
    private ContentPageService contentPageService;

    /**
     * Get page content untuk client (public)
     */
    @GetMapping("/pages/{pageName}")
    public ResponseEntity<ApiResponse<PageContentResponse>> getPageContent(
        @PathVariable PageName pageName
    ) {
        PageContentResponse content = contentPageService.getPageContent(pageName);
        return ResponseEntity.ok(ApiResponse.success(content));
    }
}
