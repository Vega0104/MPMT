package com.mpmt.backend.security;

import com.mpmt.backend.entity.RoleType;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext(); // Nettoyer le contexte avant chaque test
    }

    @Test
    void doFilterInternal_OPTIONS_skipsFilter() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("OPTIONS");

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userService);
    }

    @Test
    void doFilterInternal_publicEndpoint_skipsFilter() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userService);
    }

    @Test
    void doFilterInternal_noAuthHeader_skipsFilter() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/protected");
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userService);
    }

    @Test
    void doFilterInternal_invalidAuthHeader_skipsFilter() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/protected");
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userService);
    }

    @Test
    void doFilterInternal_validToken_authenticatesUser() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/protected");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");

        String userEmail = "test@example.com";
        User user = new User();
        user.setEmail(userEmail);
        user.setRole(RoleType.MEMBER);

        when(jwtService.extractEmail(anyString())).thenReturn(userEmail);
        when(userService.getUserByEmail(userEmail)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(anyString(), anyString())).thenReturn(true);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        assert authentication.getPrincipal().equals(user);
        assert authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"));
    }

    @Test
    void doFilterInternal_invalidToken_doesNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/protected");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token.here");

        String userEmail = "test@example.com";
        when(jwtService.extractEmail(anyString())).thenReturn(userEmail);
        when(userService.getUserByEmail(userEmail)).thenReturn(Optional.empty());

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }

    @Test
    void doFilterInternal_tokenValidButUserNotFound_doesNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/protected");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");

        String userEmail = "test@example.com";
        when(jwtService.extractEmail(anyString())).thenReturn(userEmail);
        when(userService.getUserByEmail(userEmail)).thenReturn(Optional.empty());

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }

    @Test
    void doFilterInternal_exception_continuesFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/protected");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
        when(jwtService.extractEmail(anyString())).thenThrow(new RuntimeException("JWT parsing error"));

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }
}
