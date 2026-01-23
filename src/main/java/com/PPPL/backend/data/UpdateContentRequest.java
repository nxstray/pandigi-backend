package com.PPPL.backend.data;

import com.PPPL.backend.model.ContentType;
import com.PPPL.backend.model.PageName;
import lombok.Data;

@Data
public class UpdateContentRequest {
    private PageName pageName;
    private String sectionKey;
    private ContentType contentType;
    private String contentValue;
    private String contentLabel;
    private Integer displayOrder;
}