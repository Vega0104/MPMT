package com.mpmt.backend.security;

import com.mpmt.backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public JwtAuthFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 0) Laisser passer TOUT préflight CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Skip JWT validation for public endpoints
        String path = request.getRequestURI();
        System.out.println("=== JWT Filter - Path: " + path);

        if (path.startsWith("/api/auth/")
                || path.startsWith("/auth/")           // si jamais des routes /auth/ non /api existent
                || path.startsWith("/hello")
                || path.startsWith("/api/hello")
                || path.startsWith("/actuator/")) {
            System.out.println("=== Skipping JWT for public endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        System.out.println("=== Authorization Header: " + (authHeader != null ? "Present" : "Missing"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("=== No valid Authorization header, continuing without auth");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            System.out.println("=== JWT extracted, length: " + jwt.length());

            final String userEmail = jwtService.extractEmail(jwt);
            System.out.println("=== Email extracted: " + userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userOpt = userService.getUserByEmail(userEmail);
                System.out.println("=== User found in DB: " + userOpt.isPresent());

                if (userOpt.isPresent() && jwtService.isTokenValid(jwt, userEmail)) {
                    System.out.println("=== Token valid, authenticating user");
                    var user = userOpt.get();

                    // Map RoleType -> GrantedAuthority "ROLE_<ROLE>"
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

                    var authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            authorities
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("=== Authentication successful");
                } else {
                    System.out.println("=== Token validation failed");
                }
            }
        } catch (Exception e) {
            System.err.println("=== JWT Filter ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}
