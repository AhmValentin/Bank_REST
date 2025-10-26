package com.example.bankcards.service;

import com.example.bankcards.entity.user.Role;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.exception.JWTValidException;
import com.example.bankcards.exception.JwtAuthenticationException;
import com.example.bankcards.service.auth.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    private final String secret = "veryLongSecretKeyForTestingPurposes1234567890";
    private final long expiration = 3600000L; // 1 час
    private final long refreshTokenExpiration = 86400000L; // 24 часа

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expiration", expiration);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", refreshTokenExpiration);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.USER);
    }

    @Test
    void generateToken_success() {
        String token = jwtService.generateToken(user);
        assertThat(token).isNotNull().isNotEmpty();
        Claims claims = jwtService.extractAllClaims(token);
        assertThat(claims.getSubject()).isEqualTo(user.getEmail());
    }

    @Test
    void generateToken_containsCorrectClaims() {
        String token = jwtService.generateToken(user);
        Claims claims = jwtService.extractAllClaims(token);
        assertThat(claims.getSubject()).isEqualTo(user.getEmail());
        assertThat(claims.get("role")).isEqualTo(Role.USER.name());
        assertThat(claims.get("fullName")).isEqualTo("John Doe");
        assertThat(claims.get("type")).isEqualTo("access");
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void generateRefreshToken_success() {
        String refreshToken = jwtService.generateRefreshToken(user);
        assertThat(refreshToken).isNotNull().isNotEmpty();
        Claims claims = jwtService.extractAllClaims(refreshToken);
        assertThat(claims.getSubject()).isEqualTo(user.getEmail());
    }

    @Test
    void generateRefreshToken_containsCorrectClaims() {
        String refreshToken = jwtService.generateRefreshToken(user);
        Claims claims = jwtService.extractAllClaims(refreshToken);
        assertThat(claims.getSubject()).isEqualTo(user.getEmail());
        assertThat(claims.get("role")).isEqualTo(Role.USER.name());
        assertThat(claims.get("fullName")).isEqualTo("John Doe");
        assertThat(claims.get("type")).isEqualTo("refresh");
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void extractUsername_success() {
        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo(user.getEmail());
    }

    @Test
    void extractAllClaims_success() {
        String token = jwtService.generateToken(user);
        Claims claims = jwtService.extractAllClaims(token);
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(user.getEmail());
        assertThat(claims.get("role")).isEqualTo(Role.USER.name());
        assertThat(claims.get("fullName")).isEqualTo("John Doe");
        assertThat(claims.getIssuedAt()).isBefore(new Date());
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    void invalidToken_throwsException() {
        String invalidToken = "invalid.token.value";
        assertThrows(JWTValidException.class, () -> jwtService.extractUsername(invalidToken));
    }

    @Test
    void extractAllClaims_expiredToken_throwsException() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String expiredToken = jwtService.generateToken(user);
        assertThrows(JwtAuthenticationException.class, () -> jwtService.extractAllClaims(expiredToken));
    }

    @Test
    void isTokenValid_expiredToken_throwsJwtAuthenticationException() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String expiredToken = jwtService.generateToken(user);
        assertThrows(JwtAuthenticationException.class, () -> jwtService.isTokenValid(expiredToken));
    }

    @Test
    void generateToken_differentUsers_produceDifferentTokens() {
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("test2@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setRole(Role.ADMIN);
        String token1 = jwtService.generateToken(user);
        String token2 = jwtService.generateToken(user2);
        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtService.extractUsername(token1)).isEqualTo(user.getEmail());
        assertThat(jwtService.extractUsername(token2)).isEqualTo(user2.getEmail());
    }
}