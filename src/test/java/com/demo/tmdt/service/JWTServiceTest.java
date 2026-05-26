package com.demo.tmdt.service;

import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JWTServiceTest {

    private final JWTService jwtService = new JWTService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                jwtService,
                "SECRET_KEY",
                "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-extra-secret"
        );
        jwtService.validateSecretKey();
    }

    @Test
    void generateRefreshTokenUsesOneDayExpiryByDefault() throws Exception {
        String token = jwtService.generateRefreshToken("user-id", "session-id", false);

        SignedJWT jwt = jwtService.verifyToken(token);

        assertThat(jwtService.getUserId(jwt)).isEqualTo("user-id");
        assertThat(jwtService.getSessionId(jwt)).isEqualTo("session-id");
        assertThat(jwtService.getType(jwt)).isEqualTo("refresh");
        assertThat(tokenLifetime(jwt)).isBetween(Duration.ofHours(23), Duration.ofHours(25));
    }

    @Test
    void generateRefreshTokenUsesThirtyDayExpiryForRememberMe() throws Exception {
        String token = jwtService.generateRefreshToken("user-id", "session-id", true);

        SignedJWT jwt = jwtService.verifyToken(token);

        assertThat(jwtService.getType(jwt)).isEqualTo("refresh");
        assertThat(tokenLifetime(jwt)).isBetween(Duration.ofDays(29), Duration.ofDays(31));
    }

    private Duration tokenLifetime(SignedJWT jwt) throws Exception {
        Date issuedAt = jwt.getJWTClaimsSet().getIssueTime();
        Date expiresAt = jwt.getJWTClaimsSet().getExpirationTime();

        return Duration.between(issuedAt.toInstant(), expiresAt.toInstant());
    }
}
