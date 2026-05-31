package org.example.datasensefx.model;

import java.time.LocalDateTime;

/**
 * Modelo que representa un usuario del sistema.
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String nombre;
    private String passwordHash;
    private Rol rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;

    // Constructor vacío
    public User() {
    }

    // Constructor completo
    public User(int id, String username, String email, String nombre, String passwordHash, Rol rol,
                boolean activo, LocalDateTime fechaCreacion, LocalDateTime ultimoAcceso) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.ultimoAcceso = ultimoAcceso;
    }

    // Constructor para crear nuevo usuario (sin ID)
    public User(String username, String email, String nombre, String passwordHash, Rol rol) {
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    public User(String email, String nombre, String password, Rol rol) {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", rol=" + rol +
                ", activo=" + activo +
                ", fechaCreacion=" + fechaCreacion +
                ", ultimoAcceso=" + ultimoAcceso +
                '}';
    }
}

