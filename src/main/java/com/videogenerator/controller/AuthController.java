package com.videogenerator.controller;

import com.videogenerator.entity.User;
import com.videogenerator.model.ApiResponse;
import com.videogenerator.model.AuthRequest;
import com.videogenerator.model.AuthResponse;
import com.videogenerator.service.AuthService;
import com.videogenerator.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        log.info("Register request received for username: {}", request.getUsername());

        try {
            AuthResponse response = authService.register(request);

            if (response.getSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("User registered successfully", response));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(response.getMessage(), "/api/auth/register"));
            }
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed: " + e.getMessage(), "/api/auth/register"));
        }
    }

    /**
     * Login user and return JWT token
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login request received for username: {}", request.getUsername());

        try {
            AuthResponse response = authService.login(request);

            if (response.getSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("Login successful", response));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(response.getMessage(), "/api/auth/login"));
            }
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Login failed: " + e.getMessage(), "/api/auth/login"));
        }
    }

    /**
     * Get current authenticated user details
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        log.info("Get current user request received");

        try {
            User currentUser = authService.getCurrentUser();
            return ResponseEntity.ok(ApiResponse.success("Current user retrieved", currentUser));
        } catch (Exception e) {
            log.error("Error retrieving current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving user: " + e.getMessage(), "/api/auth/me"));
        }
    }

    /**
     * Validate JWT token
     * POST /api/auth/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        log.info("Token validation request received");

        try {
            Boolean isValid = jwtUtil.validateToken(token);
            Boolean isNotExpired = jwtUtil.validateTokenAndNotExpired(token);

            if (isValid && isNotExpired) {
                String username = jwtUtil.getUsernameFromToken(token);
                return ResponseEntity.ok(ApiResponse.success("Token is valid",new Object() {
                            public String username = this.username;
                            public Boolean valid = true;
                        }));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Token is invalid or expired", "/api/auth/validate"));
            }
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token validation failed: " + e.getMessage(), "/api/auth/validate"));
        }
    }

    /**
     * Refresh JWT token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String token) {
        log.info("Token refresh request received");

        try {
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid token", "/api/auth/refresh"));
            }

            String username = jwtUtil.getUsernameFromToken(token);
            User user = authService.getCurrentUser();
            String newToken = jwtUtil.generateToken(username);

            AuthResponse response = AuthResponse.builder()
                    .success(true)
                    .message("Token refreshed successfully")
                    .token(newToken)
                    .type("Bearer")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();

            return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Token refresh failed: " + e.getMessage(), "/api/auth/refresh"));
        }
    }

    /**
     * Health check endpoint
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        log.info("Health check request received");
        return ResponseEntity.ok(ApiResponse.success("Auth service is running", null));
    }
}
