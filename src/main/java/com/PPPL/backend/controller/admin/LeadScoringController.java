package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.data.lead.LeadAnalysisDTO;
import com.PPPL.backend.data.lead.LeadScoringResponse;
import com.PPPL.backend.security.JwtUtil;
import com.PPPL.backend.service.admin.LeadScoringService;
import com.PPPL.backend.service.admin.LeadScoringService.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lead-scoring")
public class LeadScoringController {

    @Autowired
    private LeadScoringService leadScoringService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Analyze lead using AI (manual trigger by admin)
     * Rate Limited: 15 requests per 2 minutes (Redis-based)
     */
    @PostMapping("/analyze/{idRequest}")
    public ResponseEntity<ApiResponse<LeadScoringResponse>> analyzeLead(
            @PathVariable Integer idRequest,
            @RequestHeader("Authorization") String authHeader) {
        
        Integer adminId = extractAdminId(authHeader);
        LeadScoringResponse result = leadScoringService.analyzeLead(idRequest, adminId);
        long remaining = leadScoringService.getRemainingRequests(adminId);

        String message = "Lead berhasil dianalisa. Remaining requests: " + remaining + "/15";

        return ResponseEntity.ok()
            .header("X-Rate-Limit-Remaining", String.valueOf(remaining))
            .header("X-Rate-Limit-Limit", "15")
            .body(ApiResponse.success(message, result));
    }

    /**
     * Batch analyze all unanalyzed leads
     * Rate Limited: 15 requests per 2 minutes
     */
    @PostMapping("/analyze-all")
    public ResponseEntity<ApiResponse<BatchAnalysisResult>> analyzeAllPendingLeads(
            @RequestHeader("Authorization") String authHeader) {
        
        Integer adminId = extractAdminId(authHeader);
        BatchAnalysisResult result = leadScoringService.analyzeAllPendingLeads(adminId);

        String message;
        if (result.totalPending == 0) {
            message = "Tidak ada lead yang perlu dianalisa";
        } else if (result.rateLimitedCount > 0) {
            message = String.format(
                "Batch analysis dihentikan karena rate limit. " +
                "Berhasil: %d, Gagal: %d, Belum diproses: %d",
                result.successCount, result.failedCount, result.rateLimitedCount
            );
        } else {
            message = String.format(
                "Batch analysis selesai! Berhasil: %d, Gagal: %d dari %d lead",
                result.successCount, result.failedCount, result.totalPending
            );
        }

        return ResponseEntity.ok()
            .body(ApiResponse.success(message, result));
    }

    /**
     * Get all leads with analysis results (for dashboard)
     * CACHED: 5 minutes
     */
    @GetMapping("/results")
    public ResponseEntity<ApiResponse<List<LeadAnalysisDTO>>> getAllLeadAnalysis() {
        List<LeadAnalysisDTO> results = leadScoringService.getAllLeadAnalysis();
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * Get leads by priority (HOT, WARM, COLD)
     * CACHED per priority
     */
    @GetMapping("/results/priority/{priority}")
    public ResponseEntity<ApiResponse<List<LeadAnalysisDTO>>> getLeadsByPriority(
            @PathVariable String priority) {
        
        List<LeadAnalysisDTO> results = leadScoringService.getLeadsByPriority(priority);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * Get statistics for dashboard
     * CACHED: 5 minutes
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<LeadStatistics>> getLeadStatistics() {
        LeadStatistics stats = leadScoringService.getLeadStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Get rate limit info for current admin
     */
    @GetMapping("/rate-limit-info")
    public ResponseEntity<ApiResponse<RateLimitInfo>> getRateLimitInfo(
            @RequestHeader("Authorization") String authHeader) {
        
        Integer adminId = extractAdminId(authHeader);
        RateLimitInfo info = leadScoringService.getRateLimitInfo(adminId);
        return ResponseEntity.ok(ApiResponse.success(info));
    }

    // Private Helper Methods
    /**
     * Extract admin ID from Authorization header
     */
    private Integer extractAdminId(String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer"
        return jwtUtil.getAdminIdFromToken(token);
    }
}