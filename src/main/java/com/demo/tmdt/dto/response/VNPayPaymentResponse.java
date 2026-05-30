package com.demo.tmdt.dto.response;

public record VNPayPaymentResponse(
        String thanhToanId,
        String donHangId,
        Integer tongTien,
        String vnpTxnRef,
        String paymentUrl
) {
}
