package com.example.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors() // ✅ 启用自定义跨域配置
                .and()
                .csrf(csrf -> csrf.disable()) // ✅ 禁用 CSRF 防护（可以根据需求选择性禁用）
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",              // ✅ 首页或根路径
                                "/auth/**",       // ✅ 登录注册接口
                                "/api/**",        // ✅ 放行 dish 上传等 API
                                "/oauth2/**"      // ✅ Google 登录跳转
                        ).permitAll()
                        .anyRequest().authenticated() // ✅ 需要认证的请求
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://localhost:5173/oauth-success", true) // ✅ 登录成功后跳转的 URL
                );

        return http.build();
    }

    // ✅ 配置跨域支持，允许前端携带 Cookie
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // ✅ 前端地址
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // ✅ 允许的请求方法
        config.setAllowedHeaders(Arrays.asList("*")); // ✅ 允许的请求头
        config.setAllowCredentials(true); // ✅ 允许携带 Cookie（token、session）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // ✅ 配置所有路径的跨域规则
        return source;
    }
}
