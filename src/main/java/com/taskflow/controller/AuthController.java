package com.taskflow.controller;

import com.taskflow.dto.LoginRequest;
import com.taskflow.dto.UserResponse;
import com.taskflow.model.User;
import com.taskflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request.email(), request.password());
        return ResponseEntity.ok(UserResponse.from(user));
    }
}
