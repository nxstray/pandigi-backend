package com.PPPL.backend.controller;

import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.model.enums.StatusRequest;
import com.PPPL.backend.model.layanan.RequestLayanan;
import com.PPPL.backend.repository.RequestLayananRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    @Autowired
    private RequestLayananRepository requestLayananRepository;

    /**
     * Helper method to check if a date falls within the specified month and year.
     */

    private boolean sameMonth(Date date, int month, int year) {
        if (date == null) return false;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) == month &&
               cal.get(Calendar.YEAR) == year;
    }

    private String safeSkor(String skor) {
        return skor != null ? skor.toUpperCase() : null;
    }

    private String safeNamaKlien(RequestLayanan r) {
        if (r.getKlien() == null || r.getKlien().getNamaKlien() == null) {
            return "Klien tidak diketahui";
        }
        return r.getKlien().getNamaKlien();
    }

    /**
     * Endpoint to get monthly lead statistics categorized by priority scores.
     */

    @GetMapping("/monthly-lead-stats")
    public ResponseEntity<ApiResponse<MonthlyLeadStats>> getMonthlyLeadStats(
            @RequestParam(defaultValue = "6") int months) {

        List<RequestLayanan> allRequests = requestLayananRepository.findAll();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat monthFormat =
                new SimpleDateFormat("MMM yyyy", new Locale("id", "ID"));

        List<MonthData> monthlyData = new ArrayList<>();

        for (int i = months - 1; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -i);

            int targetMonth = cal.get(Calendar.MONTH);
            int targetYear = cal.get(Calendar.YEAR);
            String label = monthFormat.format(cal.getTime());

            List<RequestLayanan> monthRequests = allRequests.stream()
                    .filter(r -> sameMonth(r.getTglRequest(), targetMonth, targetYear))
                    .toList();

            long hot = monthRequests.stream()
                    .filter(r -> "HOT".equalsIgnoreCase(safeSkor(r.getSkorPrioritas())))
                    .count();

            long warm = monthRequests.stream()
                    .filter(r -> "WARM".equalsIgnoreCase(safeSkor(r.getSkorPrioritas())))
                    .count();

            long cold = monthRequests.stream()
                    .filter(r -> "COLD".equalsIgnoreCase(safeSkor(r.getSkorPrioritas())))
                    .count();

            long unscored = monthRequests.stream()
                    .filter(r -> r.getSkorPrioritas() == null)
                    .count();

            monthlyData.add(new MonthData(label, hot, warm, cold, unscored));
        }

        return ResponseEntity.ok(
                ApiResponse.success(new MonthlyLeadStats(monthlyData))
        );
    }

    /**
     * Endpoint to get lead trend statistics over a specified number of months.
     */

    @GetMapping("/lead-trend")
    public ResponseEntity<ApiResponse<LeadTrendStats>> getLeadTrend(
            @RequestParam(defaultValue = "6") int months) {

        List<RequestLayanan> allRequests = requestLayananRepository.findAll();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat monthFormat =
                new SimpleDateFormat("MMM yyyy", new Locale("id", "ID"));

        List<TrendData> trendData = new ArrayList<>();
        long previousCount = 0;

        for (int i = months - 1; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -i);

            int targetMonth = cal.get(Calendar.MONTH);
            int targetYear = cal.get(Calendar.YEAR);
            String label = monthFormat.format(cal.getTime());

            long count = allRequests.stream()
                    .filter(r -> sameMonth(r.getTglRequest(), targetMonth, targetYear))
                    .count();

            double growthRate = previousCount > 0
                    ? ((double) (count - previousCount) / previousCount) * 100
                    : 0;

            trendData.add(new TrendData(
                    label,
                    count,
                    Math.round(growthRate * 10) / 10.0
            ));

            previousCount = count;
        }

        return ResponseEntity.ok(
                ApiResponse.success(new LeadTrendStats(trendData))
        );
    }

    /**
     * Endpoint to get conversion rate statistics over a specified number of months.
     */

    @GetMapping("/conversion-rate")
    public ResponseEntity<ApiResponse<ConversionStats>> getConversionRate(
            @RequestParam(defaultValue = "6") int months) {

        List<RequestLayanan> allRequests = requestLayananRepository.findAll();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat monthFormat =
                new SimpleDateFormat("MMM yyyy", new Locale("id", "ID"));

        List<ConversionData> conversionData = new ArrayList<>();

        for (int i = months - 1; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -i);

            int targetMonth = cal.get(Calendar.MONTH);
            int targetYear = cal.get(Calendar.YEAR);
            String label = monthFormat.format(cal.getTime());

            List<RequestLayanan> monthRequests = allRequests.stream()
                    .filter(r -> sameMonth(r.getTglRequest(), targetMonth, targetYear))
                    .toList();

            long total = monthRequests.size();
            long verified = monthRequests.stream()
                    .filter(r -> r.getStatus() == StatusRequest.VERIFIKASI)
                    .count();

            long rejected = monthRequests.stream()
                    .filter(r -> r.getStatus() == StatusRequest.DITOLAK)
                    .count();

            long pending = monthRequests.stream()
                    .filter(r -> r.getStatus() == StatusRequest.MENUNGGU_VERIFIKASI)
                    .count();

            double conversionRate = total > 0
                    ? ((double) verified / total) * 100
                    : 0;

            conversionData.add(new ConversionData(
                    label,
                    total,
                    verified,
                    rejected,
                    pending,
                    Math.round(conversionRate * 10) / 10.0
            ));
        }

        return ResponseEntity.ok(
                ApiResponse.success(new ConversionStats(conversionData))
        );
    }

    /**
     * Endpoint to get recent activities based on AI analysis.
     */

    @GetMapping("/recent-activities")
    public ResponseEntity<ApiResponse<List<ActivityData>>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {

        List<ActivityData> activities = requestLayananRepository.findAll().stream()
                .filter(r -> Boolean.TRUE.equals(r.getAiAnalyzed())
                        && r.getTglAnalisaAi() != null)
                .sorted(Comparator.comparing(RequestLayanan::getTglAnalisaAi).reversed())
                .limit(limit)
                .map(r -> {
                    String nama = safeNamaKlien(r);
                    String skor = r.getSkorPrioritas() != null
                            ? r.getSkorPrioritas()
                            : "UNKNOWN";

                    return new ActivityData(
                            r.getIdRequest(),
                            nama,
                            skor,
                            r.getTglAnalisaAi(),
                            String.format(
                                    "Lead <strong>%s</strong> telah dianalisa sebagai <strong>%s</strong> Lead",
                                    nama,
                                    skor
                            )
                    );
                })
                .toList();

        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    /** ==================== DATA CLASSES ==================== */
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