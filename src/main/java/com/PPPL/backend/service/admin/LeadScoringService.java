package com.PPPL.backend.service.admin;

import com.PPPL.backend.config.cache.RateLimiterRedisConfig;
import com.PPPL.backend.data.lead.LeadAnalysisDTO;
import com.PPPL.backend.data.lead.LeadScoringResponse;
import com.PPPL.backend.handler.RateLimitExceededException;
import com.PPPL.backend.handler.ResourceNotFoundException;
import com.PPPL.backend.model.layanan.RequestLayanan;
import com.PPPL.backend.repository.layanan.RequestLayananRepository;
import com.PPPL.backend.service.lead.GeminiService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LeadScoringService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private RequestLayananRepository requestLayananRepository;

    @Autowired
    private RateLimiterRedisConfig rateLimiterRedisConfig;

    /**
     * Analyze single lead with rate limit check
     */
    public LeadScoringResponse analyzeLead(Integer idRequest, Integer adminId) {
        // Check rate limit
        checkRateLimit(adminId);

        // Validate request exists
        if (!requestLayananRepository.existsById(idRequest)) {
            throw new ResourceNotFoundException("Request dengan ID " + idRequest + " tidak ditemukan");
        }

        // Process analysis (cached in GeminiService)
        LeadScoringResponse result = geminiService.reAnalyzeLead(idRequest);
        
        log.info("Lead {} analyzed by admin {}", idRequest, adminId);
        return result;
    }

    /**
     * Batch analyze all unanalyzed leads
     */
    public BatchAnalysisResult analyzeAllPendingLeads(Integer adminId) {
        // Get pending leads
        List<RequestLayanan> pendingLeads = requestLayananRepository
            .findAll()
            .stream()
            .filter(r -> r.getAiAnalyzed() == null || !r.getAiAnalyzed())
            .collect(Collectors.toList());

        if (pendingLeads.isEmpty()) {
            log.info("No pending leads to analyze");
            return new BatchAnalysisResult(0, 0, 0, 0);
        }

        int success = 0;
        int failed = 0;
        int rateLimited = 0;

        for (RequestLayanan lead : pendingLeads) {
            try {
                // Check rate limit for each iteration
                checkRateLimit(adminId);

                geminiService.reAnalyzeLead(lead.getIdRequest());
                success++;

            } catch (RateLimitExceededException e) {
                // Stop if rate limit exceeded
                rateLimited = pendingLeads.size() - success - failed;
                log.warn("Batch analysis stopped due to rate limit. Success: {}, Failed: {}, Remaining: {}", 
                    success, failed, rateLimited);
                break;

            } catch (Exception e) {
                failed++;
                log.error("Failed to analyze lead {}: {}", lead.getIdRequest(), e.getMessage());
            }
        }

        log.info("Batch analysis completed. Success: {}, Failed: {}, Rate limited: {}, Total: {}", 
            success, failed, rateLimited, pendingLeads.size());

        return new BatchAnalysisResult(success, failed, rateLimited, pendingLeads.size());
    }

    /**
     * Get all lead analysis results (cached)
     */
    @Cacheable(value = "leadScoring", key = "'all_results'")
    public List<LeadAnalysisDTO> getAllLeadAnalysis() {
        log.info("CACHE MISS: Fetching all lead analysis from DB");

        return requestLayananRepository.findAll()
            .stream()
            .map(this::mapToLeadAnalysisDTO)
            .sorted((a, b) -> {
                // Sort: unanalyzed first, then by priority, then by date
                if (a.getAiAnalyzed() != b.getAiAnalyzed()) {
                    return a.getAiAnalyzed() ? 1 : -1;
                }
                if (a.getSkorPrioritas() != null && b.getSkorPrioritas() != null) {
                    int priorityCompare = getPriorityOrder(a.getSkorPrioritas()) - 
                                        getPriorityOrder(b.getSkorPrioritas());
                    if (priorityCompare != 0) return priorityCompare;
                }
                return b.getTglRequest().compareTo(a.getTglRequest());
            })
            .collect(Collectors.toList());
    }

    /**
     * Get leads by priority (cached per priority)
     */
    @Cacheable(value = "leadScoring", key = "'priority_' + #priority")
    public List<LeadAnalysisDTO> getLeadsByPriority(String priority) {
        log.info("CACHE MISS: Fetching leads with priority {}", priority);

        // Validate priority
        if (!priority.equals("HOT") && !priority.equals("WARM") && !priority.equals("COLD")) {
            throw new IllegalArgumentException("Priority harus HOT, WARM, atau COLD");
        }

        return requestLayananRepository
            .findAll()
            .stream()
            .filter(r -> priority.equalsIgnoreCase(r.getSkorPrioritas()))
            .map(this::mapToLeadAnalysisDTO)
            .sorted((a, b) -> b.getTglRequest().compareTo(a.getTglRequest()))
            .collect(Collectors.toList());
    }

    /**
     * Get lead statistics (cached)
     */
    @Cacheable(value = "leadStatistics", key = "'stats'")
    public LeadStatistics getLeadStatistics() {
        log.info("CACHE MISS: Calculating lead statistics");

        List<RequestLayanan> allLeads = requestLayananRepository.findAll();

        long totalLeads = allLeads.size();
        long analyzedLeads = allLeads.stream()
            .filter(r -> r.getAiAnalyzed() != null && r.getAiAnalyzed())
            .count();
        long hotLeads = allLeads.stream()
            .filter(r -> "HOT".equals(r.getSkorPrioritas()))
            .count();
        long warmLeads = allLeads.stream()
            .filter(r -> "WARM".equals(r.getSkorPrioritas()))
            .count();
        long coldLeads = allLeads.stream()
            .filter(r -> "COLD".equals(r.getSkorPrioritas()))
            .count();

        return new LeadStatistics(totalLeads, analyzedLeads, hotLeads, warmLeads, coldLeads);
    }

    /**
     * Get rate limit info for admin
     */
    public RateLimitInfo getRateLimitInfo(Integer adminId) {
        long remaining = rateLimiterRedisConfig.getAiRemainingTokens(adminId);
        int maxRequests = rateLimiterRedisConfig.getAiMaxRequests();
        long windowMinutes = rateLimiterRedisConfig.getAiWindowSeconds() / 60;

        return new RateLimitInfo(maxRequests, remaining, (int) windowMinutes);
    }

    /**
     * Get remaining requests for admin
     */
    public long getRemainingRequests(Integer adminId) {
        return rateLimiterRedisConfig.getAiRemainingTokens(adminId);
    }

    // Private Helper Methods

    /**
     * Check rate limit and throw exception if exceeded
     */
    private void checkRateLimit(Integer adminId) {
        if (!rateLimiterRedisConfig.allowAiRequest(adminId)) {
            long waitForRefill = rateLimiterRedisConfig.getAiTimeUntilReset(adminId);
            throw new RateLimitExceededException(
                "Rate limit exceeded. Maksimal 15 requests per 2 menit. Coba lagi dalam " + waitForRefill + " detik.",
                waitForRefill
            );
        }
    }

    /**
     * Map RequestLayanan entity to LeadAnalysisDTO
     */
    private LeadAnalysisDTO mapToLeadAnalysisDTO(RequestLayanan r) {
        LeadAnalysisDTO dto = new LeadAnalysisDTO();
        dto.setIdRequest(r.getIdRequest());
        dto.setNamaKlien(r.getKlien().getNamaKlien());
        dto.setEmailKlien(r.getKlien().getEmailKlien());
        dto.setPerusahaan(r.getPerusahaan());
        dto.setLayanan(r.getLayanan().getNamaLayanan());
        dto.setSkorPrioritas(r.getSkorPrioritas());
        dto.setKategoriLead(r.getKategoriLead());
        dto.setAlasanSkor(r.getAlasanSkor());
        dto.setStatusRequest(r.getStatus().toString());
        dto.setTglRequest(r.getTglRequest());
        dto.setTglAnalisaAi(r.getTglAnalisaAi());
        dto.setAiAnalyzed(r.getAiAnalyzed() != null ? r.getAiAnalyzed() : false);
        return dto;
    }

    /**
     * Get priority order for sorting (HOT=1, WARM=2, COLD=3)
     */
    private int getPriorityOrder(String priority) {
        switch (priority) {
            case "HOT": return 1;
            case "WARM": return 2;
            case "COLD": return 3;
            default: return 4;
        }
    }

    // Inner Classes for Response DTOs
    public static class LeadStatistics {
        public long totalLeads;
        public long analyzedLeads;
        public long hotLeads;
        public long warmLeads;
        public long coldLeads;

        public LeadStatistics(long total, long analyzed, long hot, long warm, long cold) {
            this.totalLeads = total;
            this.analyzedLeads = analyzed;
            this.hotLeads = hot;
            this.warmLeads = warm;
            this.coldLeads = cold;
        }
    }

    public static class RateLimitInfo {
        public int maxRequests;
        public long remainingRequests;
        public int windowMinutes;

        public RateLimitInfo(int max, long remaining, int window) {
            this.maxRequests = max;
            this.remainingRequests = remaining;
            this.windowMinutes = window;
        }
    }

    public static class BatchAnalysisResult {
        public int successCount;
        public int failedCount;
        public int rateLimitedCount;
        public int totalPending;

        public BatchAnalysisResult(int success, int failed, int rateLimited, int total) {
            this.successCount = success;
            this.failedCount = failed;
            this.rateLimitedCount = rateLimited;
            this.totalPending = total;
        }
    }
}