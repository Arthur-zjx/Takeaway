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

    /** Exposed for AdminAuthController to generate JWT */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /** Use BCrypt to verify hashed passwords stored in the database */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. Insert JWT filter before the username/password filter
        http.addFilterBefore(
                new JwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        http
                .cors().and()
                .csrf(csrf -> csrf.disable())

                // 2. Return 401 for unauthenticated /api/** requests instead of redirecting
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        )
                )

                // 3. Route authorization configuration
                .authorizeHttpRequests(authorize -> authorize
                        // —— Public ——
                        .requestMatchers("/", "/auth/register", "/auth/login", "/oauth2/**", "/logout")
                        .permitAll()
                        // Current user endpoint requires authentication
                        .requestMatchers(HttpMethod.GET, "/auth/me")
                        .authenticated()
                        // Allow admin login endpoint
                        .requestMatchers("/api/admin/login")
                        .permitAll()
                        // Admin endpoints require ADMIN role
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")
                        // User endpoints require authentication (Bearer or session)
                        .requestMatchers("/api/**")
                        .authenticated()
                        // Allow all other static resources
                        .anyRequest()
                        .permitAll()
                )

                // Keep existing OAuth2 login
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://localhost:5173/oauth-success", true)
                )
                // Keep existing logout logic
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

    /** CORS configuration: allow cookies and Bearer tokens from the frontend */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        // Add PATCH method
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Explicitly allow common headers
        config.setAllowedHeaders(List.of(
                "Authorization", "Cache-Control", "Content-Type", "X-Requested-With"
        ));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
