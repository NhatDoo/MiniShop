package com.demo.tmdt.mapper;

import com.demo.tmdt.dto.response.SanPhamResponse;
import com.demo.tmdt.model.SanPham;
import org.springframework.stereotype.Component;

@Component
public class SanPhamMapper {

    public SanPhamResponse toResponse(SanPham sanPham) {
        return new SanPhamResponse(
                sanPham.getId(),
                sanPham.getTen(),
                sanPham.getMoTa(),
                sanPham.getGiaTien(),
                sanPham.getSoLuongTon(),
                sanPham.getDeletedAt(),
                sanPham.getHinhanhUrl()
        );
    }
}
