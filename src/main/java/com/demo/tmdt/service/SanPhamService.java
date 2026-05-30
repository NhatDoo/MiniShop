package com.demo.tmdt.service;

import com.demo.tmdt.common.exception.AppException;
import com.demo.tmdt.common.exception.ErrorCode;
import com.demo.tmdt.dto.request.SanPhamRequest;
import com.demo.tmdt.model.SanPham;
import com.demo.tmdt.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final MinioService minioService;

    public List<SanPham> getAll() {
        return sanPhamRepository.findByDeletedAtIsNull();
    }

    public SanPham getById(String id) {
        return sanPhamRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.SANPHAM_NOT_FOUND));
    }

    public SanPham create(SanPhamRequest request) {
        return create(request, null);
    }

    public SanPham create(SanPhamRequest request, MultipartFile hinhAnh) {
        String hinhanhUrl = minioService.uploadImage(hinhAnh);

        SanPham sanPham = SanPham.builder()
                .id(UUID.randomUUID().toString())
                .ten(request.ten())
                .moTa(request.moTa())
                .giaTien(request.giaTien())
                .soLuongTon(request.soLuongTon())
                .hinhanhUrl(hinhanhUrl != null ? hinhanhUrl : request.hinhanhUrl())
                .build();

        return sanPhamRepository.save(sanPham);
    }

    public SanPham update(String id, SanPhamRequest request) {
        return update(id, request, null);
    }

    public SanPham update(String id, SanPhamRequest request, MultipartFile hinhAnh) {
        SanPham sanPham = getById(id);
        String hinhanhUrl = minioService.uploadImage(hinhAnh);

        sanPham.setTen(request.ten());
        sanPham.setMoTa(request.moTa());
        sanPham.setGiaTien(request.giaTien());
        sanPham.setSoLuongTon(request.soLuongTon());
        sanPham.setHinhanhUrl(hinhanhUrl != null ? hinhanhUrl : request.hinhanhUrl());

        return sanPhamRepository.save(sanPham);
    }

    public void delete(String id) {
        SanPham sanPham = getById(id);
        sanPham.setDeletedAt(LocalDateTime.now());
        sanPhamRepository.save(sanPham);
    }
}
