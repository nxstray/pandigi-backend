package com.PPPL.backend.service.admin;

import com.PPPL.backend.controller.admin.DashboardController.*;
import com.PPPL.backend.model.enums.StatusRequest;
import com.PPPL.backend.model.layanan.RequestLayanan;
import com.PPPL.backend.repository.layanan.RequestLayananRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

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
     * Get monthly lead statistics categorized by priority scores.
     */
    public MonthlyLeadStats getMonthlyLeadStats(int months) {
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
                    .collect(Collectors.toList());

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

        return new MonthlyLeadStats(monthlyData);
    }

    /**
     * Get lead trend statistics over a specified number of months.
     */
    public LeadTrendStats getLeadTrend(int months) {
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

        return new LeadTrendStats(trendData);
    }

    /**
     * Get conversion rate statistics over a specified number of months.
     */
    public ConversionStats getConversionRate(int months) {
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
                    .collect(Collectors.toList());

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

        return new ConversionStats(conversionData);
    }

    /**
     * Get recent activities based on AI analysis.
     */
    public List<ActivityData> getRecentActivities(int limit) {
        return requestLayananRepository.findAll().stream()
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
                .collect(Collectors.toList());
    }
}