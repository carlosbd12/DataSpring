package org.example.dataspring.mapper;

import org.example.dataspring.dto.user.UserResponse;
import org.example.dataspring.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setNombre(user.getNombre())
                .setRole(user.getRole() != null ? user.getRole().name() : null)
                .setActivo(user.isActivo())
                .setFechaCreacion(user.getFechaCreacion())
                .setUltimoAcceso(user.getUltimoAcceso());
    }

    public UserResponse toCreatedResponse(User user, String temporaryPassword) {
        return toResponse(user)
                .setTemporaryPassword(temporaryPassword);
    }
}