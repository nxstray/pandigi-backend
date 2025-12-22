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

import java.util.*;

@Service
public class GeminiService {
    
    @Value("${google.gemini.api-key}")
    private String apiKey;
    
    @Value("${google.gemini.model}")
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
            // Validate API key first
            if (apiKey == null || apiKey.isEmpty() || apiKey.trim().isEmpty()) {
                throw new RuntimeException("Gemini API Key tidak ditemukan. Pastikan GEMINI_API_KEY sudah di-set di environment variables.");
            }
            
            // 1. Validasi data request
            validateLeadData(request);
            
            // 2. Build Prompt
            String prompt = buildAnalysisPrompt(request);
            
            // 3. Prepare request body untuk Gemini API
            Map<String, Object> requestBody = new HashMap<>();
            
            // Contents array
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();
            
            // Parts array
            List<Map<String, Object>> parts = new ArrayList<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            parts.add(part);
            
            content.put("parts", parts);
            contents.add(content);
            requestBody.put("contents", contents);
            
            // Generation config untuk output yang lebih konsisten
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.2);
            generationConfig.put("topK", 20);
            generationConfig.put("topP", 0.8);
            generationConfig.put("maxOutputTokens", 8192);
            requestBody.put("generationConfig", generationConfig);
            
            // Safety settings
            List<Map<String, Object>> safetySettings = new ArrayList<>();
            String[] categories = {
                "HARM_CATEGORY_HARASSMENT",
                "HARM_CATEGORY_HATE_SPEECH",
                "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                "HARM_CATEGORY_DANGEROUS_CONTENT"
            };
            for (String category : categories) {
                Map<String, Object> setting = new HashMap<>();
                setting.put("category", category);
                setting.put("threshold", "BLOCK_NONE");
                safetySettings.add(setting);
            }
            requestBody.put("safetySettings", safetySettings);
            
            // 4. Setup headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);
            
            // 5. Build URL
            String url = GEMINI_API_URL + model + ":generateContent?key=" + apiKey;
            
            // Debug logging
            System.out.println("=== GEMINI API DEBUG ===");
            System.out.println("URL: " + GEMINI_API_URL + model + ":generateContent");
            System.out.println("API Key present: " + (apiKey != null && !apiKey.isEmpty()));
            System.out.println("API Key length: " + (apiKey != null ? apiKey.length() : 0));
            System.out.println("Model: " + model);
            System.out.println("Request Body: " + objectMapper.writeValueAsString(requestBody));
            System.out.println("========================");
            
            // 6. Send request
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                String.class
            );
            
            // 7. Parse response
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("=== GEMINI RESPONSE ===");
                System.out.println(response.getBody());
                System.out.println("=======================");
                
                result = parseGeminiResponse(response.getBody());
                
                // 8. Save hasil analisa ke database
                saveAnalysisResult(request.getIdRequest(), result);
            } else {
                throw new RuntimeException("Gemini API error: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            result.setSkorPrioritas("ERROR");
            result.setKategori("UNKNOWN");
            result.setAlasan("Gagal menganalisa: " + e.getMessage());
            result.setConfidence(0);
            result.setRekomendasi("Silakan coba lagi atau hubungi administrator");
        }
        
        return result;
    }
    
    /**
     * Validasi data lead sebelum analisa
     */
    private void validateLeadData(LeadScoringRequest request) {
        if (request.getIdRequest() == null) {
            throw new IllegalArgumentException("ID Request tidak boleh null");
        }
        if (request.getNamaKlien() == null || request.getNamaKlien().isEmpty()) {
            throw new IllegalArgumentException("Nama klien tidak boleh kosong");
        }
    }
    
    /**
     * Build prompt untuk analisa lead (SHORTENED VERSION)
     */
    private String buildAnalysisPrompt(LeadScoringRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analisa lead scoring untuk klien:\n\n");
        
        prompt.append("DATA:\n");
        prompt.append("- Nama: ").append(request.getNamaKlien()).append("\n");
        prompt.append("- Perusahaan: ").append(orDefault(request.getPerusahaan(), "N/A")).append("\n");
        prompt.append("- Email: ").append(request.getEmailKlien()).append("\n");
        prompt.append("- Layanan: ").append(request.getLayanan()).append("\n");
        prompt.append("- Budget: ").append(orDefault(request.getAnggaran(), "N/A")).append("\n");
        prompt.append("- Timeline: ").append(orDefault(request.getWaktuImplementasi(), "N/A")).append("\n");
        prompt.append("- Pesan: ").append(orDefault(request.getPesan(), "N/A")).append("\n\n");
        
        prompt.append("KRITERIA:\n");
        prompt.append("HOT: Budget >50jt, Timeline <1 bulan/urgent, Email corporate\n");
        prompt.append("WARM: Budget 20-50jt, Timeline 1-3 bulan\n");
        prompt.append("COLD: Budget <20jt/tidak tahu, Timeline fleksibel, Email personal\n\n");
        
        prompt.append("OUTPUT (JSON only, no markdown):\n");
        prompt.append("{\n");
        prompt.append("  \"skorPrioritas\": \"HOT/WARM/COLD\",\n");
        prompt.append("  \"kategori\": \"tipe bisnis\",\n");
        prompt.append("  \"alasan\": \"penjelasan singkat\",\n");
        prompt.append("  \"confidence\": 85,\n");
        prompt.append("  \"rekomendasi\": \"action items\"\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }
    
    /**
     * Helper untuk handle null values
     */
    private String orDefault(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
    
    /**
     * Parse response dari Gemini API
     */
    private LeadScoringResponse parseGeminiResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        
        // Extract text dari response
        JsonNode candidates = root.path("candidates");
        if (candidates.isEmpty()) {
            throw new RuntimeException("No candidates in Gemini response");
        }
        
        // Check finish reason
        String finishReason = candidates.get(0).path("finishReason").asText();
        System.out.println("=== FINISH REASON: " + finishReason + " ===");
        
        String aiText = candidates
            .get(0)
            .path("content")
            .path("parts")
            .get(0)
            .path("text")
            .asText();
        
        if (aiText == null || aiText.isEmpty()) {
            throw new RuntimeException("Empty response from Gemini");
        }
        
        System.out.println("=== AI TEXT BEFORE CLEANING ===");
        System.out.println(aiText);
        System.out.println("================================");
        
        // Clean JSON
        String cleanJson = aiText
            .replace("```json", "")
            .replace("```", "")
            .trim();
        
        // Handle jika response masih ada text di luar JSON
        int jsonStart = cleanJson.indexOf("{");
        int jsonEnd = cleanJson.lastIndexOf("}");
        
        if (jsonStart >= 0 && jsonEnd > jsonStart) {
            cleanJson = cleanJson.substring(jsonStart, jsonEnd + 1);
        } else {
            // JSON tidak lengkap, coba repair
            System.out.println("=== WARNING: Incomplete JSON, attempting repair ===");
            cleanJson = repairIncompleteJson(cleanJson);
        }
        
        System.out.println("=== CLEANED JSON ===");
        System.out.println(cleanJson);
        System.out.println("====================");
        
        // Parse ke LeadScoringResponse
        LeadScoringResponse response;
        try {
            response = objectMapper.readValue(cleanJson, LeadScoringResponse.class);
        } catch (Exception e) {
            System.out.println("=== PARSE ERROR: " + e.getMessage() + " ===");
            // If MAX_TOKENS, try to repair and parse again
            if ("MAX_TOKENS".equals(finishReason)) {
                cleanJson = repairIncompleteJson(aiText);
                response = objectMapper.readValue(cleanJson, LeadScoringResponse.class);
            } else {
                throw e;
            }
        }
        
        // Validasi response
        if (response.getSkorPrioritas() == null || 
            (!response.getSkorPrioritas().equals("HOT") && 
             !response.getSkorPrioritas().equals("WARM") && 
             !response.getSkorPrioritas().equals("COLD"))) {
            throw new RuntimeException("Invalid skorPrioritas: " + response.getSkorPrioritas());
        }
        
        // Fill in missing fields if any
        if (response.getRekomendasi() == null || response.getRekomendasi().isEmpty()) {
            response.setRekomendasi("Follow up sesuai prioritas lead");
        }
        
        return response;
    }
    
    /**
     * Repair incomplete JSON from truncated response
     */
    private String repairIncompleteJson(String incompleteJson) {
        // Remove any markdown
        String cleaned = incompleteJson
            .replace("```json", "")
            .replace("```", "")
            .trim();
        
        // Find JSON start
        int jsonStart = cleaned.indexOf("{");
        if (jsonStart >= 0) {
            cleaned = cleaned.substring(jsonStart);
        }
        
        // Check if it ends with closing brace
        if (!cleaned.endsWith("}")) {
            // Try to close incomplete field
            if (cleaned.contains("\"rekomendasi")) {
                // rekomendasi field is incomplete
                int lastColon = cleaned.lastIndexOf(":");
                if (lastColon > 0) {
                    cleaned = cleaned.substring(0, lastColon + 1) + " \"Follow up sesuai prioritas\"\n}";
                } else {
                    cleaned = cleaned + "\": \"Follow up sesuai prioritas\"\n}";
                }
            } else {
                // Just close the JSON
                cleaned = cleaned + "\n}";
            }
        }
        
        System.out.println("=== REPAIRED JSON ===");
        System.out.println(cleaned);
        System.out.println("=====================");
        
        return cleaned;
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
     * Digunakan oleh LeadScoringController
     */
    public LeadScoringResponse reAnalyzeLead(Integer idRequest) {
        RequestLayanan request = requestLayananRepository.findById(idRequest)
            .orElseThrow(() -> new RuntimeException("Request dengan ID " + idRequest + " tidak ditemukan"));
        
        // Build LeadScoringRequest dengan data lengkap
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