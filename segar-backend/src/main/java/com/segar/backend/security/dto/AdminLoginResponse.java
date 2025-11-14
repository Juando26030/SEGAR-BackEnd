package com.segar.backend.security.dto;

/**
 * DTO para respuesta de login de administrador
 */
public class AdminLoginResponse {
    private boolean success;
    private String username;
    private String role;
    private String message;
    private String accessToken;
    private String refreshToken;

    public AdminLoginResponse() {
    }

    public AdminLoginResponse(boolean success, String username, String role, String message) {
        this.success = success;
        this.username = username;
        this.role = role;
        this.message = message;
    }

    public AdminLoginResponse(boolean success, String username, String role, String message,
                             String accessToken, String refreshToken) {
        this.success = success;
        this.username = username;
        this.role = role;
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

