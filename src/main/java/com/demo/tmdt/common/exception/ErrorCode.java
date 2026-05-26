package com.demo.tmdt.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1000, "Invalid request", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1001, "Email is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1002, "Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(1004, "Phone number is invalid", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1005, "Email already exists", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1007, "Invalid refresh token", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(1008, "User not found", HttpStatus.NOT_FOUND),
    SESSION_NOT_FOUND(1009, "Session not found", HttpStatus.NOT_FOUND),
    WRONG_PASSWORD(1010, "Wrong password", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN(1011, "Invalid access token", HttpStatus.UNAUTHORIZED),
    INVALID_GOOGLE_ID_TOKEN(1012, "Invalid Google ID token", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
