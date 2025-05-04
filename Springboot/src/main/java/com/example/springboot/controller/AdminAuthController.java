package com.example.springboot.controller;

import com.example.springboot.config.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AuthenticationManager authManager;

    public AdminAuthController(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> creds) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.get("username"), creds.get("password")
                )
        );
        String role = auth.getAuthorities()
                .iterator().next()
                .getAuthority()
                .replace("ROLE_", "");
        String token = JwtUtils.generateToken(creds.get("username"), role);
        return Map.of("token", token, "role", role);
    }
}