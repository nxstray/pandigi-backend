package com.PPPL.backend.controller.admin;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.service.admin.DashboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Endpoint to get monthly lead statistics categorized by priority scores.
     */
    @GetMapping("/monthly-lead-stats")
    public ResponseEntity<ApiResponse<MonthlyLeadStats>> getMonthlyLeadStats(
            @RequestParam(defaultValue = "6") int months) {
        
        MonthlyLeadStats stats = dashboardService.getMonthlyLeadStats(months);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Endpoint to get lead trend statistics over a specified number of months.
     */
    @GetMapping("/lead-trend")
    public ResponseEntity<ApiResponse<LeadTrendStats>> getLeadTrend(
            @RequestParam(defaultValue = "6") int months) {
        
        LeadTrendStats stats = dashboardService.getLeadTrend(months);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Endpoint to get conversion rate statistics over a specified number of months.
     */
    @GetMapping("/conversion-rate")
    public ResponseEntity<ApiResponse<ConversionStats>> getConversionRate(
            @RequestParam(defaultValue = "6") int months) {
        
        ConversionStats stats = dashboardService.getConversionRate(months);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Endpoint to get recent activities based on AI analysis.
     */
    @GetMapping("/recent-activities")
    public ResponseEntity<ApiResponse<List<ActivityData>>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ActivityData> activities = dashboardService.getRecentActivities(limit);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    // Data Transfer Objects (DTOs) for API responses
    public static class MonthlyLeadStats {
        public List<MonthData> data;
        public MonthlyLeadStats(List<MonthData> data) {
            this.data = data;
        }
    }

    public static class MonthData {
        public String month;
        public long hot;
        public long warm;
        public long cold;
        public long unscored;

        public MonthData(String month, long hot, long warm, long cold, long unscored) {
            this.month = month;
            this.hot = hot;
            this.warm = warm;
            this.cold = cold;
            this.unscored = unscored;
        }
    }

    public static class LeadTrendStats {
        public List<TrendData> data;
        public LeadTrendStats(List<TrendData> data) {
            this.data = data;
        }
    }

    public static class TrendData {
        public String month;
        public long count;
        public double growthRate;

        public TrendData(String month, long count, double growthRate) {
            this.month = month;
            this.count = count;
            this.growthRate = growthRate;
        }
    }

    public static class ConversionStats {
        public List<ConversionData> data;
        public ConversionStats(List<ConversionData> data) {
            this.data = data;
        }
    }

    public static class ConversionData {
        public String month;
        public long total;
        public long verified;
        public long rejected;
        public long pending;
        public double conversionRate;

        public ConversionData(String month, long total, long verified,
                              long rejected, long pending, double conversionRate) {
            this.month = month;
            this.total = total;
            this.verified = verified;
            this.rejected = rejected;
            this.pending = pending;
            this.conversionRate = conversionRate;
        }
    }

    public static class ActivityData {
        public Integer idRequest;
        public String namaKlien;
        public String skorPrioritas;
        public Date tglAnalisaAi;
        public String description;

        public ActivityData(Integer idRequest, String namaKlien,
                            String skorPrioritas, Date tglAnalisaAi,
                            String description) {
            this.idRequest = idRequest;
            this.namaKlien = namaKlien;
            this.skorPrioritas = skorPrioritas;
            this.tglAnalisaAi = tglAnalisaAi;
            this.description = description;
        }
    }
}