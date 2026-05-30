package com.demo.tmdt.dto.response;

import java.time.LocalDateTime;

public record SanPhamResponse(
        String id,
        String ten,
        String moTa,
        Integer giaTien,
        Integer soLuongTon,
        LocalDateTime deletedAt,
        String hinhanhUrl
) {
}
