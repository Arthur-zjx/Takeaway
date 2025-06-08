package com.example.springboot.controller;

import com.example.springboot.config.JwtUtils;
import com.example.springboot.model.User;
import com.example.springboot.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")  // Allow cross‑origin requests from the frontend
public class UserAuthController {

    private final AuthService authService;
    private final AuthenticationManager authManager;

    @Autowired
    public UserAuthController(AuthService authService,
                              AuthenticationManager authManager) {
        this.authService   = authService;
        this.authManager   = authManager;
    }

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        try {
            User user = authService.register(
                    payload.get("username"),
                    payload.get("email"),
                    payload.get("password")
            );
            return ResponseEntity.ok(Map.of("message", "Registration successful!"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }

    // Login endpoint: returns token, id, username, role, message
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        try {
            // 1. Spring Security validates username/password
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.get("username"),
                            creds.get("password")
                    )
            );

            // 2. Load the full User object
            User user = authService.login(
                    creds.get("username"),
                    creds.get("password")
            );

            // 3. Extract role from Authentication and remove prefix
            String role = auth.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority()
                    .replace("ROLE_", "");

            // 4. Generate JWT token
            String token = JwtUtils.generateToken(user.getUsername(), role);

            // 5. Build response body
            Map<String, String> resp = new HashMap<>();
            resp.put("token",    token);
            resp.put("id",       user.getId().toString());
            resp.put("username", user.getUsername());
            resp.put("role",     role);
            resp.put("message",  "Login successful!");

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }

    // Admin account update endpoint
    @PutMapping("/update-admin/{id}")
    public ResponseEntity<?> updateAdminAccount(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload
    ) {
        try {
            authService.updateAdminAccount(
                    id,
                    payload.get("username"),
                    payload.get("password")
            );
            return ResponseEntity.ok(Map.of("message", "Account updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }

    // Logout endpoint (optional ‑ Spring Security /logout can also be used)
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        // Get username from Spring Security context
        String username = authentication.getName();
        // Use AuthService to fetch full User object (requires findByUsername in AuthService)
        User user = authService.findByUsername(username);
        // Build response data
        Map<String, Object> resp = new HashMap<>();
        resp.put("id",       user.getId());
        resp.put("username", user.getUsername());
        resp.put("email",    user.getEmail());
        // Return role if available in User object
        resp.put("role",     user.getRole());
        return ResponseEntity.ok(resp);
    }
}
