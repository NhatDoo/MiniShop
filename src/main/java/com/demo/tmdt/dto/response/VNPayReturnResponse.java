package com.demo.tmdt.dto.response;

import com.demo.tmdt.enums.TrangThaiThanhToan;

public record VNPayReturnResponse(
        String thanhToanId,
        String donHangId,
        String vnpTxnRef,
        TrangThaiThanhToan trangThai,
        boolean validSignature,
        String responseCode,
        String transactionNo,
        String bankCode
) {
}
