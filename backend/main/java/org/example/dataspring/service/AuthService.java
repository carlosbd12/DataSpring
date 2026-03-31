package org.example.dataspring.service;

import org.example.dataspring.dto.auth.ChangePasswordRequest;
import org.example.dataspring.dto.auth.LoginRequest;
import org.example.dataspring.dto.auth.LoginResponse;
import org.example.dataspring.dto.auth.RegisterRequest;
import org.example.dataspring.dto.user.UserResponse;
import org.example.dataspring.entity.Role;
import org.example.dataspring.entity.User;
import org.example.dataspring.exception.ResourceNotFoundException;
import org.example.dataspring.mapper.UserMapper;
import org.example.dataspring.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public LoginResponse login(LoginRequest request) {
        String usernameOrEmail = safeTrim(request.getUsernameOrEmail());
        String password = request.getPassword();

        if (usernameOrEmail.isEmpty()) {
            return new LoginResponse(false, null, null, null, null, null, false, null,
                    "Username o email vacío");
        }

        if (password == null || password.trim().isEmpty()) {
            return new LoginResponse(false, null, null, null, null, null, false, null,
                    "Contraseña vacía");
        }

        Optional<User> userOptional = findByUsernameOrEmail(usernameOrEmail);

        if (userOptional.isEmpty()) {
            return new LoginResponse(false, null, null, null, null, null, false, null,
                    "Usuario no encontrado");
        }

        User user = userOptional.get();

        if (!user.isActivo()) {
            return new LoginResponse(false, null, null, null, null, null, false, null,
                    "El usuario está desactivado");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return new LoginResponse(false, null, null, null, null, null, false, null,
                    "Credenciales incorrectas");
        }

        user.setUltimoAcceso(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return new LoginResponse(
                true,
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getNombre(),
                savedUser.getRole().name(),
                savedUser.isActivo(),
                savedUser.getUltimoAcceso(),
                "Login correcto"
        );
    }

    public UserResponse register(RegisterRequest request) {
        String username = safeTrim(request.getUsername());
        String email = safeTrim(request.getEmail()).toLowerCase(Locale.ROOT);
        String nombre = safeTrim(request.getNombre());
        String password = request.getPassword();
        String roleValue = safeTrim(request.getRole());

        validateRegisterData(username, email, nombre, password);

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El username ya está registrado: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado: " + email);
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setNombre(nombre);
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setRole(parseRole(roleValue));
        newUser.setActivo(true);
        newUser.setFechaCreacion(LocalDateTime.now());
        newUser.setUltimoAcceso(null);

        User createdUser = userRepository.save(newUser);
        return userMapper.toResponse(createdUser);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + userId));

        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();

        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña actual es obligatoria");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }

        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword.trim()));
        userRepository.save(user);
    }

    public UserResponse getUserByUsernameOrEmail(String usernameOrEmail) {
        User user = findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usernameOrEmail));

        return userMapper.toResponse(user);
    }

    private Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        String value = safeTrim(usernameOrEmail);

        if (value.contains("@")) {
            return userRepository.findByEmail(value.toLowerCase(Locale.ROOT));
        }

        return userRepository.findByUsername(value);
    }

    private void validateRegisterData(String username, String email, String nombre, String password) {
        if (username.isEmpty() || username.length() < 3) {
            throw new IllegalArgumentException("Username inválido: mínimo 3 caracteres");
        }

        if (email.isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Nombre vacío");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Contraseña inválida: mínimo 6 caracteres");
        }
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role.toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Rol inválido: " + role);
        }
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}