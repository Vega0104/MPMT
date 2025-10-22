package com.mpmt.backend.controller;

import com.mpmt.backend.DTO.LoginRequest;
import com.mpmt.backend.DTO.SignupRequest;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.entity.RoleType;
import com.mpmt.backend.service.UserService;
import com.mpmt.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest("testuser", "test@example.com", "password");
        loginRequest = new LoginRequest("test@example.com", "password");
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(RoleType.MEMBER);
    }

    @Test
    void signup_Success() {
        when(userService.emailExists(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userService.createUser(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(anyString())).thenReturn("token");

        ResponseEntity<?> response = authController.signup(signupRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("token", responseBody.get("token"));
        assertEquals("testuser", responseBody.get("username"));
        assertEquals("MEMBER", responseBody.get("role"));
    }

    @Test
    void signup_EmailAlreadyExists() {
        when(userService.emailExists(anyString())).thenReturn(true);

        ResponseEntity<?> response = authController.signup(signupRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Email déjà utilisé", responseBody.get("error"));
    }

    @Test
    void login_Success() {
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(anyString())).thenReturn("token");

        ResponseEntity<?> response = authController.login(loginRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("token", responseBody.get("token"));
        assertEquals("testuser", responseBody.get("username"));
        assertEquals(1L, responseBody.get("userId"));
    }

    @Test
    void login_Failure() {
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(loginRequest);

        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Email ou mot de passe incorrect", response.getBody());
    }

    @Test
    void test() {
        ResponseEntity<String> response = authController.test();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("✅ AuthController is working", response.getBody());
    }

    @Test
    void init() {
        assertDoesNotThrow(() -> authController.init());
    }
}
