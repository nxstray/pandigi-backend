package com.PPPL.backend.data.content;

import com.PPPL.backend.model.enums.ContentType;
import com.PPPL.backend.model.enums.PageName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentPageDTO {
    private Integer idContent;
    private PageName pageName;
    private String sectionKey;
    private ContentType contentType;
    private String contentValue;
    private String contentLabel;
    private Integer displayOrder;
    private Boolean isActive;
    private String updatedByName;
    private Date updatedAt;
}