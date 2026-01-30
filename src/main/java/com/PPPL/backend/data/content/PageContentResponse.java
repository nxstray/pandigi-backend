package com.PPPL.backend.data.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageContentResponse {
    private String pageName;
    private Map<String, Object> content;
}