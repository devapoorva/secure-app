package com.example.javaapp.models;

import java.util.UUID;

public class LoginResponse {
    private String username;
    private UUID id;
    private String token;

    public LoginResponse(String username, UUID id, String token) {
        this.username = username;
        this.id = id;
        this.token = token;
    }

    public LoginResponse() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
