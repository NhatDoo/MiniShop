package com.demo.tmdt.dto.response;

public record CartItemResponse(
        String id,
        String sanPhamId,
        String tenSanPham,
        Integer giaTien,
        Integer soLuong,
        Integer thanhTien
) {
}
