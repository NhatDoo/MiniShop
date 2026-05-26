package com.demo.tmdt.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "INVALID_REQUEST")
        String idToken,

        Boolean rememberMe,

        String deviceInfo
) {
    public boolean shouldRemember() {
        return Boolean.TRUE.equals(rememberMe);
    }
}
