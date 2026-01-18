package com.PPPL.backend.security;

public record JwtUserPrincipal(
        String username,
        Integer userId,
        String role
) {}
