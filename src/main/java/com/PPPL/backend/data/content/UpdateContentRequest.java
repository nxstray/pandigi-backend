package com.PPPL.backend.data.content;

import com.PPPL.backend.model.enums.ContentType;
import com.PPPL.backend.model.enums.PageName;

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