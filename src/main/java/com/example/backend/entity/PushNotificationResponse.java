package com.example.backend.entity;

import lombok.Data;

@Data

public class PushNotificationResponse {

    private int status;
    private String message;

    public PushNotificationResponse() {
    }

    public PushNotificationResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

}