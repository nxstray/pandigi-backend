package com.PPPL.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. Skip JWT FILTER untuk WebSocket / SockJS
        if (path.startsWith("/api/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("[JWT FILTER] Request URI: {}", path);

        try {
            String jwt = getJwtFromRequest(request);

            // Tidak spam warning lagi
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {

                String username = jwtUtil.getUsernameFromToken(jwt);
                String role = jwtUtil.getRoleFromToken(jwt);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_" + role)
                                )
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("[JWT FILTER] Authenticated user: {} with role {}", username, role);
            }

        } catch (Exception ex) {
            logger.error("[JWT FILTER] Authentication error", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT from Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
