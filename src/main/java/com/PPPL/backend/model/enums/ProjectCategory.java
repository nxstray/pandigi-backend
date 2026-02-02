package com.PPPL.backend.model.enums;

public enum ProjectCategory {
    WEB_DEVELOPMENT("Web Development"),
    MOBILE_APP("Mobile Application"),
    UI_UX_DESIGN("UI/UX Design"),
    DIGITAL_MARKETING("Digital Marketing"),
    AI_ML("AI & Machine Learning"),
    CLOUD_INFRASTRUCTURE("Cloud Infrastructure"),
    OTHER("Other");
    
    private final String displayName;
    
    ProjectCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}