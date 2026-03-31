package org.example.dataspring.dto.user;

import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String role;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;
    private String temporaryPassword;

    public Long getId() {
        return id;
    }

    public UserResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserResponse setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public UserResponse setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String getRole() {
        return role;
    }

    public UserResponse setRole(String role) {
        this.role = role;
        return this;
    }

    public boolean isActivo() {
        return activo;
    }

    public UserResponse setActivo(boolean activo) {
        this.activo = activo;
        return this;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public UserResponse setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
        return this;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public UserResponse setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
        return this;
    }

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public UserResponse setTemporaryPassword(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
        return this;
    }
}