package com.demo.tmdt.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "EMAIL_INVALID")
        @Email(message = "EMAIL_INVALID")
        String email,

        @NotBlank(message = "PASSWORD_INVALID")
        @Size(min = 6, message = "PASSWORD_INVALID")
        String password,

        @NotBlank(message = "USERNAME_INVALID")
        @Size(min = 3, message = "USERNAME_INVALID")
        String name,

        @Pattern(regexp = "^(|\\+?[0-9]{9,15})$", message = "PHONE_INVALID")
        String sdt
) {}
