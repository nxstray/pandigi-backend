package com.PPPL.backend.service;

import com.PPPL.backend.data.LeadScoringRequest;
import com.PPPL.backend.data.LeadScoringResponse;
import com.PPPL.backend.model.RequestLayanan;
import com.PPPL.backend.repository.RequestLayananRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiService {
    
    @Value("${google.gemini.api-key}")
    private String apiKey;
    
    @Value("${google.gemini.model:gemini-1.5-flash}")
    private String model;
    
    @Autowired
    private RequestLayananRepository requestLayananRepository;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    
    /**
     * Analisa lead menggunakan Gemini AI
     */
    public LeadScoringResponse analyzeLead(LeadScoringRequest request) {
        LeadScoringResponse result = new LeadScoringResponse();
        
        try {
            // 1. Build Prompt
            String prompt = buildAnalysisPrompt(request);
            
            // 2. Prepare request body untuk Gemini API
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> parts = new HashMap<>();
            
            parts.put("text", prompt);
            content.put("parts", new Object[]{parts});
            requestBody.put("contents", new Object[]{content});
            
            // 3. Setup headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 4. Build URL
            String url = GEMINI_API_URL + model + ":generateContent?key=" + apiKey;
            
            // 5. Send request
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                String.class
            );
            
            // 6. Parse response
            result = parseGeminiResponse(response.getBody());
            
            // 7. Save hasil analisa ke database
            saveAnalysisResult(request.getIdRequest(), result);
            
        } catch (Exception e) {
            e.printStackTrace();
            result.setSkorPrioritas("ERROR");
            result.setKategori("UNKNOWN");
            result.setAlasan("Gagal menganalisa: " + e.getMessage());
            result.setConfidence(0);
        }
        
        return result;
    }
    
    /**
     * Build prompt untuk analisa lead
     */
    private String buildAnalysisPrompt(LeadScoringRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Kamu adalah AI Lead Scoring Expert untuk perusahaan digital agency. ");
        prompt.append("Analisa prospek klien berikut dan berikan scoring:\n\n");
        
        prompt.append("DATA KLIEN:\n");
        prompt.append("- Nama: ").append(request.getNamaKlien()).append("\n");
        prompt.append("- Perusahaan: ").append(request.getPerusahaan()).append("\n");
        prompt.append("- Email: ").append(request.getEmailKlien()).append("\n");
        prompt.append("- No Telp: ").append(request.getNoTelp()).append("\n");
        prompt.append("- Layanan: ").append(request.getLayanan()).append("\n");
        prompt.append("- Topic: ").append(request.getTopic()).append("\n");
        prompt.append("- Anggaran: ").append(request.getAnggaran()).append("\n");
        prompt.append("- Waktu: ").append(request.getWaktuImplementasi()).append("\n");
        prompt.append("- Pesan: ").append(request.getPesan()).append("\n\n");
        
        prompt.append("ATURAN SCORING:\n");
        prompt.append("HOT (Prioritas Tinggi):\n");
        prompt.append("- Budget > 50 juta\n");
        prompt.append("- Kebutuhan mendesak (segera/< 1 bulan)\n");
        prompt.append("- Pesan jelas dan spesifik\n");
        prompt.append("- Email corporate/perusahaan\n\n");
        
        prompt.append("WARM (Prioritas Menengah):\n");
        prompt.append("- Budget 20-50 juta\n");
        prompt.append("- Timeline 1-3 bulan\n");
        prompt.append("- Pesan cukup jelas\n\n");
        
        prompt.append("COLD (Prioritas Rendah):\n");
        prompt.append("- Budget < 20 juta atau tidak disebutkan\n");
        prompt.append("- Timeline fleksibel/tidak jelas\n");
        prompt.append("- Pesan terlalu umum\n\n");
        
        prompt.append("OUTPUT FORMAT (WAJIB JSON, tidak ada text tambahan):\n");
        prompt.append("{\n");
        prompt.append("  \"skorPrioritas\": \"HOT/WARM/COLD\",\n");
        prompt.append("  \"kategori\": \"kategori bisnis\",\n");
        prompt.append("  \"alasan\": \"penjelasan singkat\",\n");
        prompt.append("  \"confidence\": 85,\n");
        prompt.append("  \"rekomendasi\": \"langkah selanjutnya\"\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }
    
    /**
     * Parse response dari Gemini API
     */
    private LeadScoringResponse parseGeminiResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        
        // Extract text dari response
        String aiText = root.path("candidates")
            .get(0)
            .path("content")
            .path("parts")
            .get(0)
            .path("text")
            .asText();
        
        // Clean JSON (remove markdown code blocks)
        String cleanJson = aiText
            .replace("```json", "")
            .replace("```", "")
            .trim();
        
        // Parse ke LeadScoringResponse
        return objectMapper.readValue(cleanJson, LeadScoringResponse.class);
    }
    
    /**
     * Simpan hasil analisa ke database
     */
    private void saveAnalysisResult(Integer idRequest, LeadScoringResponse result) {
        RequestLayanan request = requestLayananRepository.findById(idRequest)
            .orElseThrow(() -> new RuntimeException("Request tidak ditemukan"));
        
        request.setSkorPrioritas(result.getSkorPrioritas());
        request.setKategoriLead(result.getKategori());
        request.setAlasanSkor(result.getAlasan());
        request.setTglAnalisaAi(new Date());
        request.setAiAnalyzed(true);
        
        requestLayananRepository.save(request);
    }
    
    /**
     * Analisis ulang lead (untuk refresh scoring)
     */
    public LeadScoringResponse reAnalyzeLead(Integer idRequest) {
        RequestLayanan request = requestLayananRepository.findById(idRequest)
            .orElseThrow(() -> new RuntimeException("Request tidak ditemukan"));
        
        LeadScoringRequest scoringRequest = new LeadScoringRequest();
        scoringRequest.setIdRequest(idRequest);
        scoringRequest.setNamaKlien(request.getKlien().getNamaKlien());
        scoringRequest.setPerusahaan(request.getPerusahaan());
        scoringRequest.setLayanan(request.getLayanan().getNamaLayanan());
        scoringRequest.setTopic(request.getTopic());
        scoringRequest.setPesan(request.getPesan());
        scoringRequest.setAnggaran(request.getAnggaran());
        scoringRequest.setWaktuImplementasi(request.getWaktuImplementasi());
        scoringRequest.setEmailKlien(request.getKlien().getEmailKlien());
        scoringRequest.setNoTelp(request.getKlien().getNoTelp());
        
        return analyzeLead(scoringRequest);
    }
}