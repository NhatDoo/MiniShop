package com.demo.tmdt.mapper;

import com.demo.tmdt.dto.response.CartItemResponse;
import com.demo.tmdt.dto.response.CartResponse;
import com.demo.tmdt.model.ChiTietGioHang;
import com.demo.tmdt.model.GioHang;
import com.demo.tmdt.model.SanPham;
import com.demo.tmdt.repository.ChiTietGioHangRepository;
import com.demo.tmdt.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GioHangMapper {

    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final SanPhamRepository sanPhamRepository;

    public CartResponse toResponse(GioHang gioHang) {
        List<ChiTietGioHang> items = chiTietGioHangRepository.findByGioHangId(gioHang.getId());
        Map<String, SanPham> products = sanPhamRepository.findAllById(
                        items.stream().map(ChiTietGioHang::getSanPhamId).toList()
                ).stream()
                .collect(Collectors.toMap(SanPham::getId, Function.identity()));

        List<CartItemResponse> itemResponses = items.stream()
                .map(item -> {
                    SanPham sanPham = products.get(item.getSanPhamId());
                    Integer lineTotal = item.getGiaTien() * item.getSoLuong();
                    return new CartItemResponse(
                            item.getId(),
                            item.getSanPhamId(),
                            sanPham == null ? null : sanPham.getTen(),
                            item.getGiaTien(),
                            item.getSoLuong(),
                            lineTotal
                    );
                })
                .toList();

        Integer total = itemResponses.stream()
                .map(CartItemResponse::thanhTien)
                .reduce(0, Integer::sum);

        return new CartResponse(gioHang.getId(), gioHang.getUserId(), total, itemResponses);
    }
}
