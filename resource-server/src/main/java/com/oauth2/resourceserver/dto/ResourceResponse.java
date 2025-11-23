package com.oauth2.resourceserver.dto;

import java.time.LocalDateTime;

public class ResourceResponse {
    private String message;
    private String user;
    private LocalDateTime timestamp;

    public ResourceResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ResourceResponse(String message, String user) {
        this.message = message;
        this.user = user;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}