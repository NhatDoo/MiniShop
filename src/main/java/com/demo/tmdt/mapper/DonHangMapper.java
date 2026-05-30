package com.demo.tmdt.mapper;

import com.demo.tmdt.dto.response.OrderItemResponse;
import com.demo.tmdt.dto.response.OrderResponse;
import com.demo.tmdt.model.ChiTietDonHang;
import com.demo.tmdt.model.DonHang;
import com.demo.tmdt.model.SanPham;
import com.demo.tmdt.repository.ChiTietDonHangRepository;
import com.demo.tmdt.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DonHangMapper {

    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final SanPhamRepository sanPhamRepository;

    public OrderResponse toResponse(DonHang donHang) {
        List<ChiTietDonHang> items = chiTietDonHangRepository.findByDonHangId(donHang.getId());
        Map<String, SanPham> products = sanPhamRepository.findAllById(
                        items.stream().map(ChiTietDonHang::getSanPhamId).toList()
                ).stream()
                .collect(Collectors.toMap(SanPham::getId, Function.identity()));

        List<OrderItemResponse> itemResponses = items.stream()
                .map(item -> {
                    SanPham sanPham = products.get(item.getSanPhamId());
                    Integer lineTotal = item.getGiaTien() * item.getSoLuong();
                    return new OrderItemResponse(
                            item.getId(),
                            item.getSanPhamId(),
                            sanPham == null ? null : sanPham.getTen(),
                            item.getGiaTien(),
                            item.getSoLuong(),
                            lineTotal
                    );
                })
                .toList();

        return new OrderResponse(
                donHang.getId(),
                donHang.getUserId(),
                donHang.getTongTien(),
                donHang.getTrangThai(),
                donHang.getDeletedAt(),
                itemResponses
        );
    }
}
