package com.PPPL.backend.config;

import com.PPPL.backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                // permit all public endpoints
                .requestMatchers(
                    "/",
                    "/health",
                    "/actuator/health",
                    "/api/auth/**",
                    "/api/public/**",
                    "/uploads/**",
                    "/ws/**",
                    "/api/ws/**"
                ).permitAll()

                // Admin and role-based access control
                .requestMatchers("/api/admin/**")
                    .hasAnyRole("SUPER_ADMIN", "MANAGER")

                .requestMatchers("/api/manager/**")
                    .hasAnyRole("MANAGER", "SUPER_ADMIN")

                .requestMatchers("/api/karyawan/**")
                    .hasRole("SUPER_ADMIN")

                .requestMatchers("/api/klien/**")
                    .hasAnyRole("SUPER_ADMIN", "MANAGER")

                .requestMatchers("/api/layanan/**")
                    .hasRole("SUPER_ADMIN")

                .requestMatchers("/api/request-layanan/**")
                    .hasAnyRole("SUPER_ADMIN", "MANAGER")

                .requestMatchers("/api/rekap/**")
                    .hasAnyRole("SUPER_ADMIN", "MANAGER")

                // Fallback: all other requests need authentication
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}