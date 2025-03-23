package com.example.springboot.controller;

import com.example.springboot.model.User;
import com.example.springboot.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")  // Allow requests from frontend
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        try {
            User user = authService.register(
                    payload.get("username"),
                    payload.get("email"),
                    payload.get("password")
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration successful!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            User user = authService.login(payload.get("username"), payload.get("password"));

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful!");
            response.put("username", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
