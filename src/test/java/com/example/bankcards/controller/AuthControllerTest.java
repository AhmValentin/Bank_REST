package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.AuthResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        loginRequest = new LoginRequest("test@example.com", "password");
        authResponse = new AuthResponse(
                "access-token-123",
                "refresh-token-123",
                "test@example.com",
                "USER",
                "John Doe"
        );
    }

    @Test
    void login_success() {
        when(authService.login(loginRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(authResponse);
        assertThat(response.getBody().token()).isEqualTo("access-token-123");
        assertThat(response.getBody().email()).isEqualTo("test@example.com");
        assertThat(response.getBody().role()).isEqualTo("USER");
        assertThat(response.getBody().fullName()).isEqualTo("John Doe");

        verify(authService).login(loginRequest);
    }
}
