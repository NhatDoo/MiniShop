package com.demo.tmdt.dto.response;

import com.demo.tmdt.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String id,
        String userId,
        Integer tongTien,
        OrderStatus trangThai,
        LocalDateTime deletedAt,
        List<OrderItemResponse> items
) {
}
