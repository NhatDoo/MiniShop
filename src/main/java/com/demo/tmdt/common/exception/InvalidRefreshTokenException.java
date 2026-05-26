package com.demo.tmdt.common.exception;

public class InvalidRefreshTokenException extends AppException {

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN);
    }
}
