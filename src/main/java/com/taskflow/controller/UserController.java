package com.taskflow.controller;

// import com.taskflow.dto.CreateUserRequest;
import com.taskflow.dto.UserResponse;
// import com.taskflow.model.User;
import com.taskflow.service.UserService;
// import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")    // Prefijo: /api/users
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> users = userService.getAll()
                .stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

}