package com.PPPL.backend.service.email;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class EmailService {

    @Value("${resend.api.key:}")
    private String resendApiKey;

    @Value("${resend.from.email:noreply@pandawadigital.web.id}")
    private String fromEmail;

    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * Send HTML email via Resend API
     */
    public void sendEmail(String to, String subject, String htmlContent) {
        // Fallback: if API key is not configured, only log (for development)
        if (resendApiKey == null || resendApiKey.trim().isEmpty()) {
            log.warn("Resend API key not configured. Email NOT sent.");
            log.info("Would send email to: {}", to);
            log.info("Subject: {}", subject);
            return;
        }

        String jsonPayload = String.format("""
            {
                "from": "%s",
                "to": ["%s"],
                "subject": "%s",
                "html": %s
            }
            """,
            fromEmail,
            to,
            escapeJson(subject),
            escapeJsonString(htmlContent)
        );

        RequestBody body = RequestBody.create(
            jsonPayload,
            MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
            .url("https://api.resend.com/emails")
            .header("Authorization", "Bearer " + resendApiKey)
            .header("Content-Type", "application/json")
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error details";
                log.error("Resend API error: {} - {}", response.code(), errorBody);
                throw new RuntimeException("Failed to send email via Resend: " + errorBody);
            }
            log.info("Email sent successfully to: {}", to);
        } catch (IOException e) {
            log.error("Email send failed: {}", e.getMessage(), e);
            throw new RuntimeException("Email service error: " + e.getMessage(), e);
        }
    }

    /**
     * Escape string for JSON subject
     */
    private String escapeJson(String text) {
        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }

    /**
     * Escape HTML content for JSON value
     */
    private String escapeJsonString(String html) {
        String escaped = html
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
        return "\"" + escaped + "\"";
    }
}