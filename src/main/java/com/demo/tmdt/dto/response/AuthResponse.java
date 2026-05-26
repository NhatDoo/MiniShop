package com.demo.tmdt.dto.response;


public record AuthResponse(
        String accessToken,
        String refreshToken,
        boolean rememberMe
) {}
