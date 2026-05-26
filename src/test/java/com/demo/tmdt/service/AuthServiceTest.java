package com.demo.tmdt.service;

import com.demo.tmdt.dto.response.AuthResponse;
import com.demo.tmdt.model.Session;
import com.demo.tmdt.model.User;
import com.demo.tmdt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private JWTService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GoogleOidcService googleOidcService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginCreatesRememberMeRefreshTokenAndResponse() {
        User user = User.builder()
                .id("user-id")
                .email("user@example.com")
                .matKhauHash("password-hash")
                .build();
        Session session = Session.builder()
                .id("session-id")
                .rememberMe(true)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "password-hash")).thenReturn(true);
        when(sessionService.createSession(user, true, "Chrome")).thenReturn(session);
        when(jwtService.generateRefreshToken("user-id", "session-id", true)).thenReturn("refresh-token");
        when(jwtService.generateAccessToken("user-id", "session-id")).thenReturn("access-token");

        AuthResponse response = authService.login("user@example.com", "password", true, "Chrome");

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.rememberMe()).isTrue();
        verify(sessionService).saveTokenHash("session-id", "refresh-token");
    }

    @Test
    void loginWithGoogleCreatesUserWhenEmailDoesNotExist() {
        GoogleIdentity identity = new GoogleIdentity("google-sub", "new@example.com", "New User");
        User createdUser = User.builder()
                .id("new-user-id")
                .email("new@example.com")
                .ten("New User")
                .build();
        Session session = Session.builder()
                .id("session-id")
                .rememberMe(false)
                .build();

        when(googleOidcService.verify("google-id-token")).thenReturn(identity);
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(createdUser);
        when(sessionService.createSession(createdUser, false, "Chrome")).thenReturn(session);
        when(jwtService.generateRefreshToken("new-user-id", "session-id", false)).thenReturn("refresh-token");
        when(jwtService.generateAccessToken("new-user-id", "session-id")).thenReturn("access-token");

        AuthResponse response = authService.loginWithGoogle("google-id-token", false, "Chrome");

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.rememberMe()).isFalse();
        verify(sessionService).saveTokenHash("session-id", "refresh-token");
    }

    @Test
    void loginWithGoogleUsesExistingUserWhenEmailExists() {
        GoogleIdentity identity = new GoogleIdentity("google-sub", "user@example.com", "Google Name");
        User existingUser = User.builder()
                .id("user-id")
                .email("user@example.com")
                .ten("Existing User")
                .build();
        Session session = Session.builder()
                .id("session-id")
                .rememberMe(true)
                .build();

        when(googleOidcService.verify("google-id-token")).thenReturn(identity);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existingUser));
        when(sessionService.createSession(existingUser, true, "Mobile")).thenReturn(session);
        when(jwtService.generateRefreshToken("user-id", "session-id", true)).thenReturn("refresh-token");
        when(jwtService.generateAccessToken("user-id", "session-id")).thenReturn("access-token");

        AuthResponse response = authService.loginWithGoogle("google-id-token", true, "Mobile");

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.rememberMe()).isTrue();
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }
}
