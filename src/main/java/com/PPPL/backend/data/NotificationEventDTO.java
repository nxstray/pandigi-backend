package com.PPPL.backend.data;

import lombok.Data;

@Data
public class NotificationEventDTO {
    private String type;
    private String title;
    private String message;
    private String link;

    // optional
    private String email;
    private boolean sendEmail;
    private boolean broadcastAdmin;
}
