package com.example.bankcards.security;

import com.example.bankcards.service.auth.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtAuthenticationEntryPoint entryPoint;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   JwtAuthenticationEntryPoint entryPoint) {
        this.jwtService = jwtService;
        this.entryPoint = entryPoint;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getRequestURI().contains("/v3/api-docs") ||
                request.getRequestURI().contains("/swagger-ui") ||
                request.getRequestURI().contains("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            if (!jwtService.isTokenValid(jwt)) {
                throw new JwtException("Ошибка проверки подписи JWT токена");
            }

            String username = jwtService.extractUsername(jwt);
            List<SimpleGrantedAuthority> authorities = extractAuthoritiesFromToken(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            entryPoint.commence(request, response,
                    new org.springframework.security.core.AuthenticationException(e.getMessage()) {});
        }
    }

    private List<SimpleGrantedAuthority> extractAuthoritiesFromToken(String token) {
        try {
            Claims claims = jwtService.extractAllClaims(token);
            String role = claims.get("role", String.class);
            if (role != null && !role.trim().isEmpty()) {
                String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                return List.of(new SimpleGrantedAuthority(authority));
            }
        } catch (JwtException ignored) {
        }
        return Collections.emptyList();
    }
}
