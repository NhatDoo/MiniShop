package com.demo.tmdt.service;

import com.demo.tmdt.common.exception.AppException;
import com.demo.tmdt.common.exception.ErrorCode;
import com.demo.tmdt.model.Session;
import com.demo.tmdt.model.User;
import com.demo.tmdt.repository.SessionRepository;
import com.demo.tmdt.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public Session createSession(User user, boolean rememberMe, String deviceInfo)  {

        Session session = Session.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .revoked(false)
                .rememberMe(rememberMe)
                .deviceInfo(deviceInfo)
                .lastUsedAt(LocalDateTime.now())
                .expiresAt(calculateExpiry(rememberMe))
                .build();
        return sessionRepository.save(session);
    }


    public boolean isValid(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            return false;
        }

        return sessionRepository.findById(sessionId)
                .map(s -> !s.isRevoked() && !isExpired(s))
                .orElse(false);
    }

    public Optional<Session> findById(String sessionId) {
        return sessionRepository.findById(sessionId);
    }

    public boolean isExpired(Session session) {
        return session.getExpiresAt() == null || !session.getExpiresAt().isAfter(LocalDateTime.now());
    }


    public void revoke(String sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.SESSION_NOT_FOUND));

        session.setRevoked(true);
        sessionRepository.save(session);
    }


    public void touch(String sessionId) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setLastUsedAt(LocalDateTime.now());
            sessionRepository.save(session);
        });
    }

    public void saveTokenHash(String sessionId, String refreshToken) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.SESSION_NOT_FOUND));

        session.setTokenHash(hashToken(refreshToken));
        sessionRepository.save(session);
    }

    public void saveTokenHash(Session session, String refreshToken) {
        session.setTokenHash(hashToken(refreshToken));
        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    public String hashToken(String token) {
        return HashUtil.sha256(token);
    }

    public boolean matchesRefreshToken(Session session, String refreshToken) {
        if (session.getTokenHash() == null || refreshToken == null) {
            return false;
        }

        return MessageDigest.isEqual(
                session.getTokenHash().getBytes(StandardCharsets.UTF_8),
                hashToken(refreshToken).getBytes(StandardCharsets.UTF_8)
        );
    }


    private LocalDateTime calculateExpiry(boolean rememberMe) {
        if (rememberMe) {
            return LocalDateTime.now().plusDays(30);
        }
        return LocalDateTime.now().plusDays(1);
    }
}
