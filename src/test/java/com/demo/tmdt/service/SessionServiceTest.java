package com.demo.tmdt.service;

import com.demo.tmdt.model.Session;
import com.demo.tmdt.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void createSessionUsesOneDayExpiryByDefault() {
        when(sessionRepository.save(org.mockito.ArgumentMatchers.any(Session.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Session session = sessionService.createSession(null, false, "Chrome");

        Duration duration = Duration.between(session.getLastUsedAt(), session.getExpiresAt());

        assertThat(session.isRememberMe()).isFalse();
        assertThat(session.getDeviceInfo()).isEqualTo("Chrome");
        assertThat(duration).isBetween(Duration.ofHours(23), Duration.ofHours(25));
    }

    @Test
    void createSessionUsesThirtyDayExpiryWhenRememberMeIsEnabled() {
        when(sessionRepository.save(org.mockito.ArgumentMatchers.any(Session.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Session session = sessionService.createSession(null, true, "Mobile");

        Duration duration = Duration.between(session.getLastUsedAt(), session.getExpiresAt());

        assertThat(session.isRememberMe()).isTrue();
        assertThat(session.getDeviceInfo()).isEqualTo("Mobile");
        assertThat(duration).isBetween(Duration.ofDays(29), Duration.ofDays(31));
    }

    @Test
    void isValidReturnsFalseAfterSessionIsRevoked() {
        Session session = Session.builder()
                .id("session-id")
                .revoked(false)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(sessionRepository.findById("session-id")).thenReturn(Optional.of(session));

        sessionService.revoke("session-id");

        assertThat(sessionService.isValid("session-id")).isFalse();
        verify(sessionRepository).save(session);
    }

    @Test
    void matchesRefreshTokenUsesStoredHash() {
        String refreshToken = "refresh-token";
        Session session = Session.builder()
                .tokenHash(sessionService.hashToken(refreshToken))
                .build();

        assertThat(sessionService.matchesRefreshToken(session, refreshToken)).isTrue();
        assertThat(sessionService.matchesRefreshToken(session, "old-refresh-token")).isFalse();
    }
}
