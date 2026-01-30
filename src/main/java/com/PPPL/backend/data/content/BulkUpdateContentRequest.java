package com.PPPL.backend.data.content;

import lombok.Data;
import java.util.List;

@Data
public class BulkUpdateContentRequest {
    private List<UpdateContentRequest> contents;
}