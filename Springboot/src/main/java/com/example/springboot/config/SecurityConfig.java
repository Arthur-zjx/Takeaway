package com.example.springboot.config;

import com.example.springboot.config.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** 暴露给 AdminAuthController 使用，用于生成 JWT */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /** 使用 BCrypt 验证数据库中已存的哈希密码 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. 在用户名/密码过滤器前插入 JWT 过滤器
        http.addFilterBefore(
                new JwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        http
                .cors().and()
                .csrf(csrf -> csrf.disable())

                // 2. 对未认证的 /api/** 请求返回 401 而不是跳转
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        )
                )

                // 3. 路径权限配置
                .authorizeHttpRequests(authorize -> authorize
                        // —— Public ——
                        .requestMatchers("/", "/auth/register", "/auth/login", "/oauth2/**", "/logout")
                        .permitAll()
                        // 当前用户信息接口，需要登录才能访问
                        .requestMatchers(HttpMethod.GET, "/auth/me")
                        .authenticated()
                        // 管理员登录接口放行
                        .requestMatchers("/api/admin/login")
                        .permitAll()
                        // 管理端接口需 ADMIN 角色
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")
                        // 普通用户接口需登录（Bearer 或 session）
                        .requestMatchers("/api/**")
                        .authenticated()
                        // 其它静态资源等放行
                        .anyRequest()
                        .permitAll()
                )

                // 保留原有 OAuth2 登录
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://localhost:5173/oauth-success", true)
                )
                // 保留原有 Logout 逻辑
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "JWT_TOKEN")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            request.getSession().invalidate();
                            response.setStatus(200);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"Logout successful\"}");
                        })
                );

        return http.build();
    }

    /** CORS 设置：允许前端带 Cookie，也允许 Bearer Token */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        // 增加 PATCH 方法
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // 明确允许常用头
        config.setAllowedHeaders(List.of(
                "Authorization", "Cache-Control", "Content-Type", "X-Requested-With"
        ));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
