package org.example.dataspring.service;

import org.example.dataspring.dto.user.UpdateUserRequest;
import org.example.dataspring.dto.user.UserRequest;
import org.example.dataspring.dto.user.UserResponse;
import org.example.dataspring.entity.Role;
import org.example.dataspring.entity.User;
import org.example.dataspring.exception.ResourceNotFoundException;
import org.example.dataspring.mapper.UserMapper;
import org.example.dataspring.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Random random = new Random();

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse findById(Long id) {
        return userMapper.toResponse(getUserEntityById(id));
    }

    public UserResponse create(UserRequest request) {
        String username = safeTrim(request.getUsername());
        String email = safeTrim(request.getEmail()).toLowerCase(Locale.ROOT);
        String nombre = safeTrim(request.getNombre());
        String roleValue = safeTrim(request.getRole());

        validateCreateData(username, email, nombre);

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El username ya está registrado: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado: " + email);
        }

        String temporaryPassword = generateTemporaryPassword();

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setNombre(nombre);
        user.setPasswordHash(temporaryPassword);
        user.setRole(parseRole(roleValue));
        user.setActivo(request.isActivo());
        user.setFechaCreacion(LocalDateTime.now());
        user.setUltimoAcceso(null);

        User saved = userRepository.save(user);
        return userMapper.toCreatedResponse(saved, temporaryPassword);
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = getUserEntityById(id);

        String username = safeTrim(request.getUsername());
        String email = safeTrim(request.getEmail()).toLowerCase(Locale.ROOT);
        String nombre = safeTrim(request.getNombre());
        String roleValue = safeTrim(request.getRole());

        validateUpdateData(username, email, nombre);

        if (userRepository.existsByUsernameAndIdNot(username, id)) {
            throw new IllegalArgumentException("El username ya está registrado: " + username);
        }

        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException("El email ya está registrado: " + email);
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setNombre(nombre);
        user.setRole(parseRole(roleValue));
        user.setActivo(request.isActivo());

        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse activate(Long id) {
        User user = getUserEntityById(id);
        user.setActivo(true);
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse deactivate(Long id) {
        User user = getUserEntityById(id);
        user.setActivo(false);
        return userMapper.toResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id " + id);
        }
        userRepository.deleteById(id);
    }

    private User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    private void validateCreateData(String username, String email, String nombre) {
        if (username.isEmpty() || username.length() < 3) {
            throw new IllegalArgumentException("Username inválido: mínimo 3 caracteres");
        }
        if (email.isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Nombre vacío");
        }
    }

    private void validateUpdateData(String username, String email, String nombre) {
        if (username.isEmpty() || username.length() < 3) {
            throw new IllegalArgumentException("Username inválido: mínimo 3 caracteres");
        }
        if (email.isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Nombre vacío");
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

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}