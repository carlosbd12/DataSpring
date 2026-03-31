package org.example.dataspring.controller;

import org.example.dataspring.dto.auth.ChangePasswordRequest;
import org.example.dataspring.dto.auth.LoginRequest;
import org.example.dataspring.dto.auth.LoginResponse;
import org.example.dataspring.dto.auth.RegisterRequest;
import org.example.dataspring.dto.user.UserResponse;
import org.example.dataspring.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PutMapping("/change-password/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable Long userId,
                               @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userId, request);
    }
}