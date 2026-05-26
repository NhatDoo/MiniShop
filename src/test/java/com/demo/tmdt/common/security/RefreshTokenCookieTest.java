package com.demo.tmdt.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenCookieTest {

    @Test
    void createUsesOneDayMaxAgeByDefault() {
        ResponseCookie cookie = RefreshTokenCookie.create("refresh-token", false);

        assertThat(cookie.getName()).isEqualTo("refresh_token");
        assertThat(cookie.getValue()).isEqualTo("refresh-token");
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofDays(1));
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.getSameSite()).isEqualTo("Strict");
    }

    @Test
    void createUsesThirtyDayMaxAgeForRememberMe() {
        ResponseCookie cookie = RefreshTokenCookie.create("refresh-token", true);

        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofDays(30));
    }
}
