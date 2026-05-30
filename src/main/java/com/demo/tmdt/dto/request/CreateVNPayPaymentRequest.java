package com.demo.tmdt.dto.request;

public record CreateVNPayPaymentRequest(
        String bankCode,
        String locale
) {
}
