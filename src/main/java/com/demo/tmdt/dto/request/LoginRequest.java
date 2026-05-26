package com.demo.tmdt.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "EMAIL_INVALID")
        @Email(message = "EMAIL_INVALID")
        String email,

        @NotBlank(message = "PASSWORD_INVALID")
        String password,

        Boolean rememberMe,

        String deviceInfo
) {
    public boolean shouldRemember() {
        return Boolean.TRUE.equals(rememberMe);
    }
}
