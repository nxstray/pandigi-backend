package com.PPPL.backend.data;

import lombok.Data;
import java.util.List;

@Data
public class BulkUpdateContentRequest {
    private List<UpdateContentRequest> contents;
}