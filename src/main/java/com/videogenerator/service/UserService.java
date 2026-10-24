package com.videogenerator.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UserService {
    private final Map<String, String> userDatabase = new HashMap<>();

    // Method to register a new user
    public boolean registerUser(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false; // Invalid username or password
        }
        if (userDatabase.containsKey(username)) {
            return false; // User already exists
        }
        if (!isValidPassword(password)) {
            return false; // Invalid password
        }
        userDatabase.put(username, password);
        return true; // User registered successfully
    }

    // Method to look up a user
    public boolean lookupUser(String username) {
        return userDatabase.containsKey(username);
    }

    // Method to validate password strength
    private boolean isValidPassword(String password) {
        // Example password validation: at least 8 characters, one uppercase, one lowercase, one digit
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return Pattern.matches(passwordPattern, password);
    }
}