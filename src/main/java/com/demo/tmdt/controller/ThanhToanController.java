package com.demo.tmdt.controller;

import com.demo.tmdt.common.annotation.CurrentUser;
import com.demo.tmdt.common.security.UserPrincipal;
import com.demo.tmdt.dto.request.CreateVNPayPaymentRequest;
import com.demo.tmdt.dto.response.VNPayPaymentResponse;
import com.demo.tmdt.dto.response.VNPayReturnResponse;
import com.demo.tmdt.service.VNPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/thanhtoan")
@RequiredArgsConstructor
@Tag(name = "ThanhToan")
public class ThanhToanController {

    private final VNPayService vnPayService;

    @PostMapping("/vnpay/{donHangId}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create VNPay payment URL")
    public VNPayPaymentResponse createVNPayPayment(
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser,
            @PathVariable String donHangId,
            @RequestBody(required = false) CreateVNPayPaymentRequest request,
            HttpServletRequest servletRequest
    ) {
        return vnPayService.createPaymentUrl(currentUser.getUserId(), donHangId, request, servletRequest);
    }

    @GetMapping("/vnpay/return")
    @Operation(summary = "Handle VNPay return URL")
    public VNPayReturnResponse handleVNPayReturn(@RequestParam Map<String, String> params) {
        return vnPayService.handleReturn(params);
    }
}
