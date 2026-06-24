package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String user;

    public AuthResponse(String token, String user) {
        this.token = token;
        this.user = user;
    }
}
