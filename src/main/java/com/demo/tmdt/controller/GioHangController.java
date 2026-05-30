package com.demo.tmdt.controller;

import com.demo.tmdt.common.annotation.CurrentUser;
import com.demo.tmdt.common.security.UserPrincipal;
import com.demo.tmdt.dto.request.AddCartItemRequest;
import com.demo.tmdt.dto.request.UpdateCartItemRequest;
import com.demo.tmdt.dto.response.CartResponse;
import com.demo.tmdt.service.GioHangService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/giohang")
@RequiredArgsConstructor
@Tag(name = "GioHang")
@SecurityRequirement(name = "bearerAuth")
public class GioHangController {

    private final GioHangService gioHangService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my cart")
    public CartResponse getMyCart(@Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {
        return gioHangService.getMyCart(currentUser.getUserId());
    }

    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add item to my cart")
    public CartResponse addItem(
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return gioHangService.addItem(currentUser.getUserId(), request);
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update my cart item quantity")
    public CartResponse updateQuantity(
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser,
            @PathVariable String itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return gioHangService.updateQuantity(currentUser.getUserId(), itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Remove item from my cart")
    public CartResponse removeItem(
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser,
            @PathVariable String itemId
    ) {
        return gioHangService.removeItem(currentUser.getUserId(), itemId);
    }
}
