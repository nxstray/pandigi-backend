package com.PPPL.backend.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestLayananStatisticsDTO {
    private long total;
    private long menungguVerifikasi;
    private long diverifikasi;
    private long ditolak;
}