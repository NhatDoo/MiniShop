package com.demo.tmdt.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(
        @NotBlank(message = "SANPHAM_NOT_FOUND")
        String sanPhamId,

        @NotNull(message = "INVALID_QUANTITY")
        @Min(value = 1, message = "INVALID_QUANTITY")
        Integer soLuong
) {
}
