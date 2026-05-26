package com.demo.tmdt.common.exception;

import com.demo.tmdt.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getFieldErrors().isEmpty()
                ? ErrorCode.INVALID_REQUEST.name()
                : exception.getFieldErrors().get(0).getDefaultMessage();
        ErrorCode errorCode = resolveErrorCode(message, ErrorCode.INVALID_REQUEST);

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exception) {
        int status = exception.getStatusCode().value();
        ErrorCode errorCode = status == 401 || status == 403
                ? ErrorCode.UNAUTHENTICATED
                : ErrorCode.INVALID_REQUEST;

        return ResponseEntity.status(exception.getStatusCode())
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    }

    private ErrorCode resolveErrorCode(String name, ErrorCode defaultErrorCode) {
        try {
            return ErrorCode.valueOf(name);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return defaultErrorCode;
        }
    }
}
