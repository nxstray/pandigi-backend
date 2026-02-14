package com.PPPL.backend.data.content;

import com.PPPL.backend.model.enums.ContentType;
import com.PPPL.backend.model.enums.PageName;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateContentRequest {
    @NotNull(message = "Page name wajib diisi")
    private PageName pageName;

    @NotBlank(message = "Section key wajib diisi")
    @Size(max = 100, message = "Section key maksimal 100 karakter")
    private String sectionKey;

    @NotNull(message = "Content type wajib diisi")
    private ContentType contentType;

    @NotBlank(message = "Content value wajib diisi")
    @Size(max = 10000, message = "Content value maksimal 10000 karakter")
    private String contentValue;

    @Size(max = 200, message = "Content label maksimal 200 karakter")
    private String contentLabel;

    private Integer displayOrder;
}