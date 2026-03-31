package org.example.dataspring.dto.auth;

import java.time.LocalDateTime;

public class LoginResponse {

    private boolean authenticated;
    private Long userId;
    private String username;
    private String email;
    private String nombre;
    private String role;
    private boolean activo;
    private LocalDateTime ultimoAcceso;
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(boolean authenticated, Long userId, String username, String email,
                         String nombre, String role, boolean activo, LocalDateTime ultimoAcceso,
                         String message) {
        this.authenticated = authenticated;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.role = role;
        this.activo = activo;
        this.ultimoAcceso = ultimoAcceso;
        this.message = message;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRole() {
        return role;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public String getMessage() {
        return message;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}