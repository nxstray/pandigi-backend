package com.PPPL.backend.controller.client;

import com.PPPL.backend.data.client.ClientFormDTO;
import com.PPPL.backend.data.common.ApiResponse;
import com.PPPL.backend.repository.layanan.LayananRepository;
import com.PPPL.backend.service.client.ClientFormService;
import com.PPPL.backend.service.client.ClientFormService.ClientFormResult;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/form")
@CrossOrigin(origins = "*")
@Slf4j
public class ClientFormController {

    @Autowired
    private ClientFormService clientFormService;

    @Autowired
    private LayananRepository layananRepository;

    /**
     * Submit form from client
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<RequestSubmitResponse>> submitForm(
            @Valid @RequestBody ClientFormDTO form) {

        ClientFormResult result = clientFormService.submitForm(form);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Form berhasil terkirim! Tim kami akan segera menghubungi anda.",
                new RequestSubmitResponse(
                    result.idRequest,
                    result.clientName,
                    result.serviceName,
                    result.isNewClient
                )
            ));
    }

    /**
     * Get list layanan options for dropdown
     */
    @GetMapping("/layanan")
    public ResponseEntity<ApiResponse<List<LayananOption>>> getLayananOptions() {
        List<LayananOption> options = layananRepository.findAll()
            .stream()
            .map(l -> new LayananOption(
                l.getIdLayanan(),
                l.getNamaLayanan(),
                l.getKategori().toString()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(options));
    }

    // Inner classes for responses
    public static class LayananOption {
        public Integer id;
        public String name;
        public String category;

        public LayananOption(Integer id, String name, String category) {
            this.id = id;
            this.name = name;
            this.category = category;
        }
    }

    public static class RequestSubmitResponse {
        public Integer idRequest;
        public String clientName;
        public String serviceName;
        public boolean isNewClient;

        public RequestSubmitResponse(Integer idRequest, String clientName,
                String serviceName, boolean isNewClient) {
            this.idRequest = idRequest;
            this.clientName = clientName;
            this.serviceName = serviceName;
            this.isNewClient = isNewClient;
        }
    }
}