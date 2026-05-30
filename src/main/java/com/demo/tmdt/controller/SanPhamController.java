package com.demo.tmdt.controller;

import com.demo.tmdt.common.annotation.RequiredRole;
import com.demo.tmdt.dto.request.SanPhamMultipartRequest;
import com.demo.tmdt.dto.request.SanPhamRequest;
import com.demo.tmdt.dto.response.SanPhamResponse;
import com.demo.tmdt.enums.Role;
import com.demo.tmdt.mapper.SanPhamMapper;
import com.demo.tmdt.service.SanPhamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sanpham")
@RequiredArgsConstructor
@Tag(name = "SanPham")
@SecurityRequirement(name = "bearerAuth")
public class SanPhamController {

    private final SanPhamService sanPhamService;
    private final SanPhamMapper sanPhamMapper;

    @GetMapping
    @Operation(summary = "Get all products")
    public List<SanPhamResponse> getAll() {
        return sanPhamService.getAll().stream()
                .map(sanPhamMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id")
    public SanPhamResponse getById(@PathVariable String id) {
        return sanPhamMapper.toResponse(sanPhamService.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @RequiredRole(Role.ADMIN)
    @Operation(summary = "Create product")
    public SanPhamResponse create(@Valid @RequestBody SanPhamRequest request) {
        return sanPhamMapper.toResponse(sanPhamService.create(request));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @RequiredRole(Role.ADMIN)
    @Operation(summary = "Create product with image")
    public SanPhamResponse createWithImage(@Valid SanPhamMultipartRequest request) {
        return sanPhamMapper.toResponse(sanPhamService.create(request.toSanPhamRequest(), request.hinhAnh()));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequiredRole(Role.ADMIN)
    @Operation(summary = "Update product")
    public SanPhamResponse update(@PathVariable String id, @Valid @RequestBody SanPhamRequest request) {
        return sanPhamMapper.toResponse(sanPhamService.update(id, request));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiredRole(Role.ADMIN)
    @Operation(summary = "Update product with image")
    public SanPhamResponse updateWithImage(@PathVariable String id, @Valid SanPhamMultipartRequest request) {
        return sanPhamMapper.toResponse(sanPhamService.update(id, request.toSanPhamRequest(), request.hinhAnh()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiredRole(Role.ADMIN)
    @Operation(summary = "Delete product")
    public void delete(@PathVariable String id) {
        sanPhamService.delete(id);
    }
}
