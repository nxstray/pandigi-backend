package com.PPPL.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public record AuthUser(
        Integer userId,
        String username,
        String role
) {

    public static AuthUser fromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User belum terautentikasi");
        }

        String username = auth.getName();

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Role tidak ditemukan"))
                .replace("ROLE_", "");

        Integer userId = null;
        if (auth.getPrincipal() instanceof JwtUserPrincipal principal) {
            userId = principal.userId();
        }

        return new AuthUser(userId, username, role);
    }
}
