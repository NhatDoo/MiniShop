package com.demo.tmdt.service;

import com.demo.tmdt.common.exception.AppException;
import com.demo.tmdt.common.exception.ErrorCode;
import com.demo.tmdt.dto.request.AddCartItemRequest;
import com.demo.tmdt.dto.request.UpdateCartItemRequest;
import com.demo.tmdt.dto.response.CartResponse;
import com.demo.tmdt.mapper.GioHangMapper;
import com.demo.tmdt.model.ChiTietGioHang;
import com.demo.tmdt.model.GioHang;
import com.demo.tmdt.model.SanPham;
import com.demo.tmdt.repository.ChiTietGioHangRepository;
import com.demo.tmdt.repository.GioHangRepository;
import com.demo.tmdt.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final GioHangMapper gioHangMapper;

    @Transactional
    public CartResponse getMyCart(String userId) {
        GioHang gioHang = getOrCreateCart(userId);
        return gioHangMapper.toResponse(gioHang);
    }

    @Transactional(rollbackFor = Exception.class)
    public CartResponse addItem(String userId, AddCartItemRequest request) {
        GioHang gioHang = getOrCreateCart(userId);
        SanPham sanPham = getLockedProduct(request.sanPhamId());
        int quantity = validateQuantity(request.soLuong());

        reserveStock(sanPham, quantity);

        ChiTietGioHang item = chiTietGioHangRepository
                .findByGioHangIdAndSanPhamId(gioHang.getId(), sanPham.getId())
                .orElseGet(() -> ChiTietGioHang.builder()
                        .id(UUID.randomUUID().toString())
                        .gioHangId(gioHang.getId())
                        .sanPhamId(sanPham.getId())
                        .giaTien(sanPham.getGiaTien())
                        .soLuong(0)
                        .build());

        item.setSoLuong(item.getSoLuong() + quantity);
        item.setGiaTien(sanPham.getGiaTien());
        chiTietGioHangRepository.save(item);
        sanPhamRepository.save(sanPham);

        return gioHangMapper.toResponse(gioHang);
    }

    @Transactional(rollbackFor = Exception.class)
    public CartResponse updateQuantity(String userId, String itemId, UpdateCartItemRequest request) {
        GioHang gioHang = getExistingCart(userId);
        ChiTietGioHang item = getCartItem(gioHang.getId(), itemId);
        SanPham sanPham = getLockedProduct(item.getSanPhamId());
        int newQuantity = validateQuantity(request.soLuong());
        int delta = newQuantity - item.getSoLuong();

        if (delta > 0) {
            reserveStock(sanPham, delta);
        } else if (delta < 0) {
            restoreStock(sanPham, -delta);
        }

        item.setSoLuong(newQuantity);
        item.setGiaTien(sanPham.getGiaTien());
        chiTietGioHangRepository.save(item);
        sanPhamRepository.save(sanPham);

        return gioHangMapper.toResponse(gioHang);
    }

    @Transactional(rollbackFor = Exception.class)
    public CartResponse removeItem(String userId, String itemId) {
        GioHang gioHang = getExistingCart(userId);
        ChiTietGioHang item = getCartItem(gioHang.getId(), itemId);
        SanPham sanPham = getLockedProduct(item.getSanPhamId());

        restoreStock(sanPham, item.getSoLuong());
        chiTietGioHangRepository.delete(item);
        sanPhamRepository.save(sanPham);

        return gioHangMapper.toResponse(gioHang);
    }

    private GioHang getOrCreateCart(String userId) {
        return gioHangRepository.findByUserId(userId)
                .orElseGet(() -> gioHangRepository.save(GioHang.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(userId)
                        .build()));
    }

    private GioHang getExistingCart(String userId) {
        return gioHangRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
    }

    private ChiTietGioHang getCartItem(String gioHangId, String itemId) {
        return chiTietGioHangRepository.findByIdAndGioHangId(itemId, gioHangId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
    }

    private SanPham getLockedProduct(String sanPhamId) {
        return sanPhamRepository.findWithLockByIdAndDeletedAtIsNull(sanPhamId)
                .orElseThrow(() -> new AppException(ErrorCode.SANPHAM_NOT_FOUND));
    }

    private int validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }
        return quantity;
    }

    private void reserveStock(SanPham sanPham, int quantity) {
        int stock = sanPham.getSoLuongTon() == null ? 0 : sanPham.getSoLuongTon();
        if (stock < quantity) {
            throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }
        sanPham.setSoLuongTon(stock - quantity);
    }

    private void restoreStock(SanPham sanPham, int quantity) {
        int stock = sanPham.getSoLuongTon() == null ? 0 : sanPham.getSoLuongTon();
        sanPham.setSoLuongTon(stock + quantity);
    }

}
