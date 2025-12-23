package com.PPPL.backend.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientFormDTO {
    // Personal Info
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    
    // Service Request
    private Integer idLayanan;
    private String message;     // Mapping ke RequestLayanan.pesan
    
    // Additional Lead Scoring Data
    private String perusahaan;
    private String anggaran;
    private String waktuImplementasi;
    

    public String getFullName() {
        return firstName + " " + lastName;
    }
}