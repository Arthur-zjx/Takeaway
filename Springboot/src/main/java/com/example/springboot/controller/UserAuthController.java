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
@CrossOrigin(origins = "http://localhost:5173")  // 允许前端跨域
public class UserAuthController {

    private final AuthService authService;
    private final AuthenticationManager authManager;

    @Autowired
    public UserAuthController(AuthService authService,
                              AuthenticationManager authManager) {
        this.authService   = authService;
        this.authManager   = authManager;
    }

    // 注册接口
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
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 登录接口：返回 token、id、username、role、message
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        try {
            // 1. Spring Security 验证用户名/密码
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.get("username"),
                            creds.get("password")
                    )
            );

            // 2. 加载完整的 User 对象
            User user = authService.login(
                    creds.get("username"),
                    creds.get("password")
            );

            // 3. 从 Authentication 拿到角色并去除前缀
            String role = auth.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority()
                    .replace("ROLE_", "");

            // 4. 静态生成 JWT
            String token = JwtUtils.generateToken(user.getUsername(), role);

            // 5. 构建返回体
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
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 管理员账号更新接口
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
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 登出接口（可选，Spring Security 自带 /logout 也可用）
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        // 从 Spring Security 中拿到用户名
        String username = authentication.getName();
        // 使用 AuthService 查出完整 User 对象（需要在 AuthService 里实现 findByUsername 方法）
        User user = authService.findByUsername(username);
        // 构造返回数据
        Map<String, Object> resp = new HashMap<>();
        resp.put("id",       user.getId());
        resp.put("username", user.getUsername());
        resp.put("email",    user.getEmail());
        // 如果 User 对象里有角色字段，也可以返回
        resp.put("role",     user.getRole());
        return ResponseEntity.ok(resp);
    }
}
