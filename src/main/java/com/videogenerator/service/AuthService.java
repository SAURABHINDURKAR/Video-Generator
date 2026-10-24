package com.videogenerator.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.videogenerator.entity.User;
import com.videogenerator.model.AuthRequest;
import com.videogenerator.model.AuthResponse;
import com.videogenerator.repository.UserRepository;
import com.videogenerator.util.JwtUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse register(AuthRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            return AuthResponse.builder()
                    .success(false)
                    .message("Username already exists")
                    .build();
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            return AuthResponse.builder()
                    .success(false)
                    .message("Email already exists")
                    .build();
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName() != null ? request.getFullName() : request.getUsername());
        user.setIsActive(true);
        user.setRole(User.UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {} (ID: {})", savedUser.getUsername(), savedUser.getId());

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getUsername());

        return AuthResponse.builder()
                .success(true)
                .message("User registered successfully")
                .token(token)
                .type("Bearer")
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }

    /**
     * Authenticate user and return JWT token
     */
    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getIsActive()) {
                log.warn("Login attempt for inactive user: {}", request.getUsername());
                return AuthResponse.builder()
                        .success(false)
                        .message("User account is inactive")
                        .build();
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername());
            log.info("User logged in successfully: {}", user.getUsername());

            return AuthResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .token(token)
                    .type("Bearer")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();

        } catch (Exception e) {
            log.error("Login failed for user: {}, Error: {}", request.getUsername(), e.getMessage());
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid username or password")
                    .build();
        }
    }

    /**
     * Get current authenticated user
     */
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    /**
     * Validate JWT token
     */
    public Boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }
}
