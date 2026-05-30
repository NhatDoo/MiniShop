package com.demo.tmdt.controller;

import com.demo.tmdt.common.annotation.CurrentUser;
import com.demo.tmdt.common.security.UserPrincipal;
import com.demo.tmdt.dto.response.OrderResponse;
import com.demo.tmdt.service.DonHangService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/donhang")
@RequiredArgsConstructor
@Tag(name = "DonHang")
@SecurityRequirement(name = "bearerAuth")
public class DonHangController {

    private final DonHangService donHangService;

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Checkout my cart")
    public OrderResponse checkout(@Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {
        return donHangService.checkout(currentUser.getUserId());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my orders")
    public List<OrderResponse> getMyOrders(@Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {
        return donHangService.getMyOrders(currentUser.getUserId());
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my order by id")
    public OrderResponse getMyOrder(
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser,
            @PathVariable String orderId
    ) {
        return donHangService.getMyOrder(currentUser.getUserId(), orderId);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft delete my order")
    public void softDelete(
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser,
            @PathVariable String orderId
    ) {
        donHangService.softDelete(currentUser.getUserId(), orderId);
    }
}
