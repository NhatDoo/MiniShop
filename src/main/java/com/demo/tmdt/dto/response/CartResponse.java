package com.demo.tmdt.dto.response;

import java.util.List;

public record CartResponse(
        String id,
        String userId,
        Integer tongTien,
        List<CartItemResponse> items
) {
}
