package com.demo.tmdt.dto.response;

public record ErrorResponse(
        int code,
        String message
) {
}
