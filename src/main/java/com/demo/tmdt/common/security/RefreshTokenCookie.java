package com.demo.tmdt.common.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

public final class RefreshTokenCookie {

    public static final String NAME = "refresh_token";
    private static final Duration MAX_AGE = Duration.ofDays(1);
    private static final Duration REMEMBER_ME_MAX_AGE = Duration.ofDays(30);

    private RefreshTokenCookie() {
    }

    public static Optional<String> extract(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }

    public static ResponseCookie create(String token, boolean rememberMe) {
        return base(token)
                .maxAge(rememberMe ? REMEMBER_ME_MAX_AGE : MAX_AGE)
                .build();
    }

    public static ResponseCookie clear() {
        return base("")
                .maxAge(Duration.ZERO)
                .build();
    }

    private static ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict");
    }
}
