package com.PPPL.backend.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientFormDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String topic;
    private String message;
    
    private String perusahaan;
    private String anggaran;
    private String waktuImplementasi;
}