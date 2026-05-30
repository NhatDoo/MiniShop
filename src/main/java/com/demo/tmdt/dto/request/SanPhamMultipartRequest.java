package com.demo.tmdt.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record SanPhamMultipartRequest(
        @NotBlank(message = "SANPHAM_NAME_INVALID")
        String ten,

        String moTa,

        @NotNull(message = "SANPHAM_PRICE_INVALID")
        @Min(value = 0, message = "SANPHAM_PRICE_INVALID")
        Integer giaTien,

        @NotNull(message = "SANPHAM_QUANTITY_INVALID")
        @Min(value = 0, message = "SANPHAM_QUANTITY_INVALID")
        Integer soLuongTon,

        String hinhanhUrl,

        MultipartFile hinhAnh
) {
    public SanPhamRequest toSanPhamRequest() {
        return new SanPhamRequest(ten, moTa, giaTien, soLuongTon, hinhanhUrl);
    }
}
