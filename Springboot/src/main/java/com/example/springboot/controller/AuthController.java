package com.example.springboot.controller;

import com.example.springboot.model.User;
import com.example.springboot.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")  // 允许前端跨域并携带 cookie
public class AuthController {

    @Autowired
    private AuthService authService;

    // 注册接口
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
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 登录接口：返回 id、username、role
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            User user = authService.login(
                    payload.get("username"),
                    payload.get("password")
            );

            Map<String, String> response = new HashMap<>();
            response.put("message",  "Login successful!");
            response.put("id",       user.getId().toString());    // 新增：把用户 ID 放进来
            response.put("username", user.getUsername());
            response.put("role",     user.getRole());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 管理员账号更新接口（仅 ROLE_ADMIN 可访问，需在 SecurityConfig 中配置）
    @PutMapping("/update-admin/{id}")
    public ResponseEntity<?> updateAdminAccount(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String password = payload.get("password");

            authService.updateAdminAccount(id, username, password);

            return ResponseEntity.ok(Map.of("message", "Account updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 可选：登出接口（前端也可直接调用 /logout，由 Spring Security 处理）
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}