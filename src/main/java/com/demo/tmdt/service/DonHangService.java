package com.demo.tmdt.service;

import com.demo.tmdt.common.exception.AppException;
import com.demo.tmdt.common.exception.ErrorCode;
import com.demo.tmdt.dto.response.OrderResponse;
import com.demo.tmdt.enums.OrderStatus;
import com.demo.tmdt.mapper.DonHangMapper;
import com.demo.tmdt.model.ChiTietDonHang;
import com.demo.tmdt.model.ChiTietGioHang;
import com.demo.tmdt.model.DonHang;
import com.demo.tmdt.model.GioHang;
import com.demo.tmdt.repository.ChiTietDonHangRepository;
import com.demo.tmdt.repository.ChiTietGioHangRepository;
import com.demo.tmdt.repository.DonHangRepository;
import com.demo.tmdt.repository.GioHangRepository;
import com.demo.tmdt.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonHangService {

    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final DonHangMapper donHangMapper;

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse checkout(String userId) {
        GioHang gioHang = gioHangRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        List<ChiTietGioHang> cartItems = chiTietGioHangRepository.findByGioHangId(gioHang.getId());

        if (cartItems.isEmpty()) {
            throw new AppException(ErrorCode.CART_NOT_FOUND);
        }

        cartItems.forEach(item -> sanPhamRepository.findWithLockByIdAndDeletedAtIsNull(item.getSanPhamId())
                .orElseThrow(() -> new AppException(ErrorCode.SANPHAM_NOT_FOUND)));

        Integer total = cartItems.stream()
                .map(item -> item.getGiaTien() * item.getSoLuong())
                .reduce(0, Integer::sum);

        DonHang donHang = donHangRepository.save(DonHang.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .tongTien(total)
                .trangThai(OrderStatus.PENDING)
                .build());

        List<ChiTietDonHang> orderItems = cartItems.stream()
                .map(item -> ChiTietDonHang.builder()
                        .id(UUID.randomUUID().toString())
                        .donHangId(donHang.getId())
                        .sanPhamId(item.getSanPhamId())
                        .soLuong(item.getSoLuong())
                        .giaTien(item.getGiaTien())
                        .build())
                .toList();

        try {
            chiTietDonHangRepository.saveAll(orderItems);
            chiTietGioHangRepository.deleteByGioHangId(gioHang.getId());
        } catch (RuntimeException exception) {
            throw new AppException(ErrorCode.ORDER_CREATION_FAILED);
        }

        return donHangMapper.toResponse(donHang);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String userId) {
        return donHangRepository.findByUserIdAndDeletedAtIsNull(userId).stream()
                .map(donHangMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(String userId, String orderId) {
        DonHang donHang = donHangRepository.findByIdAndUserIdAndDeletedAtIsNull(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));
        return donHangMapper.toResponse(donHang);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(String userId, String orderId) {
        DonHang donHang = donHangRepository.findByIdAndUserIdAndDeletedAtIsNull(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));

        donHang.setDeletedAt(LocalDateTime.now());
        donHang.setTrangThai(OrderStatus.DELETED);
        donHangRepository.save(donHang);
    }

}
