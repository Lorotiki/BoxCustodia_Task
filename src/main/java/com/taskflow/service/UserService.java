package com.taskflow.service;

import com.taskflow.exception.ConflictException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.model.User;
import com.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado:" + id));
    }

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email ya registrado");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        return userRepository.save(user);
    }

    public User setActive(Long id, boolean active) {
        User user = getById(id);
        user.setIsActive(active);
        return userRepository.save(user);
    }

}