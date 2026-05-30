package com.demo.tmdt.service;

import com.demo.tmdt.common.config.VNPayProperties;
import com.demo.tmdt.common.exception.AppException;
import com.demo.tmdt.common.exception.ErrorCode;
import com.demo.tmdt.dto.request.CreateVNPayPaymentRequest;
import com.demo.tmdt.dto.response.VNPayPaymentResponse;
import com.demo.tmdt.dto.response.VNPayReturnResponse;
import com.demo.tmdt.enums.OrderStatus;
import com.demo.tmdt.enums.TrangThaiThanhToan;
import com.demo.tmdt.model.DonHang;
import com.demo.tmdt.model.ThanhToan;
import com.demo.tmdt.model.ThanhToanVNPay;
import com.demo.tmdt.repository.DonHangRepository;
import com.demo.tmdt.repository.ThanhToanRepository;
import com.demo.tmdt.repository.ThanhToanVNPayRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayService {

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNPAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final String PAYMENT_METHOD = "VNPAY";

    private final VNPayProperties vnPayProperties;
    private final DonHangRepository donHangRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final ThanhToanVNPayRepository thanhToanVNPayRepository;

    @Transactional(rollbackFor = Exception.class)
    public VNPayPaymentResponse createPaymentUrl(
            String userId,
            String donHangId,
            CreateVNPayPaymentRequest request,
            HttpServletRequest servletRequest
    ) {
        DonHang donHang = donHangRepository.findByIdAndUserIdAndDeletedAtIsNull(donHangId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));
        if (!OrderStatus.PENDING.equals(donHang.getTrangThai())) {
            throw new AppException(ErrorCode.ORDER_NOT_PAYABLE);
        }

        LocalDateTime now = LocalDateTime.now(VN_ZONE);
        String thanhToanId = UUID.randomUUID().toString();
        String txnRef = buildTxnRef(donHang.getId());
        String createDate = VNPAY_DATE_FORMAT.format(now);
        String expireDate = VNPAY_DATE_FORMAT.format(now.plusMinutes(vnPayProperties.getExpireMinutes()));
        String ipAddress = getClientIp(servletRequest);
        String locale = resolveLocale(request);
        String orderInfo = normalizeOrderInfo("Thanh toan don hang " + donHang.getId().replace("-", ""));

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", vnPayProperties.getVersion());
        params.put("vnp_Command", vnPayProperties.getCommand());
        params.put("vnp_TmnCode", requiredConfig(vnPayProperties.getTmnCode()));
        params.put("vnp_Amount", String.valueOf((long) donHang.getTongTien() * 100));
        params.put("vnp_CurrCode", vnPayProperties.getCurrCode());
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", vnPayProperties.getOrderType());
        params.put("vnp_Locale", locale);
        params.put("vnp_ReturnUrl", vnPayProperties.getReturnUrl());
        params.put("vnp_IpAddr", ipAddress);
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_ExpireDate", expireDate);
        if (request != null && StringUtils.hasText(request.bankCode())) {
            params.put("vnp_BankCode", request.bankCode().trim());
        }

        String hashData = buildHashData(params);
        String secureHash = hmacSha512(requiredConfig(vnPayProperties.getHashSecret()), hashData);
        log.info(
                "VNPay create tmnCode={}, hashSecretLength={}, hashData={}, secureHash={}",
                requiredConfig(vnPayProperties.getTmnCode()),
                requiredConfig(vnPayProperties.getHashSecret()).length(),
                hashData,
                secureHash
        );
        String paymentUrl = requiredConfig(vnPayProperties.getPaymentUrl()) + "?" + buildQuery(params)
                + "&vnp_SecureHash=" + secureHash;

        ThanhToan thanhToan = thanhToanRepository.save(ThanhToan.builder()
                .id(thanhToanId)
                .donHangId(donHang.getId())
                .tongTien(donHang.getTongTien())
                .trangThai(TrangThaiThanhToan.PENDING)
                .phuongThuc(PAYMENT_METHOD)
                .thoiGian(now)
                .build());

        thanhToanVNPayRepository.save(ThanhToanVNPay.builder()
                .id(UUID.randomUUID().toString())
                .thanhToanId(thanhToan.getId())
                .vnpRequestId(UUID.randomUUID().toString())
                .vnpVersion(vnPayProperties.getVersion())
                .vnpCommand(vnPayProperties.getCommand())
                .vnpTmnCode(requiredConfig(vnPayProperties.getTmnCode()))
                .vnpTxnRef(txnRef)
                .vnpOrderInfo(orderInfo)
                .vnpCreateDate(createDate)
                .vnpIpAddr(ipAddress)
                .vnpBankCode(params.get("vnp_BankCode"))
                .vnpSecureHash(secureHash)
                .thoiGian(now)
                .build());

        return new VNPayPaymentResponse(thanhToan.getId(), donHang.getId(), donHang.getTongTien(), txnRef, paymentUrl);
    }

    @Transactional(rollbackFor = Exception.class)
    public VNPayReturnResponse handleReturn(Map<String, String> responseParams) {
        String txnRef = responseParams.get("vnp_TxnRef");
        if (!StringUtils.hasText(txnRef)) {
            throw new AppException(ErrorCode.INVALID_VNPAY_RESPONSE);
        }

        ThanhToanVNPay vnPay = thanhToanVNPayRepository.findByVnpTxnRef(txnRef)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        ThanhToan thanhToan = thanhToanRepository.findById(vnPay.getThanhToanId())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        DonHang donHang = donHangRepository.findById(thanhToan.getDonHangId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        boolean validSignature = verifySignature(responseParams);
        boolean validAmount = Objects.equals(
                responseParams.get("vnp_Amount"),
                String.valueOf((long) thanhToan.getTongTien() * 100)
        );
        String responseCode = responseParams.get("vnp_ResponseCode");
        String transactionStatus = responseParams.get("vnp_TransactionStatus");
        TrangThaiThanhToan paymentStatus = validSignature
                && validAmount
                && Objects.equals(responseCode, "00")
                && Objects.equals(transactionStatus, "00")
                ? TrangThaiThanhToan.SUCCESS
                : TrangThaiThanhToan.FAILED;

        if (!TrangThaiThanhToan.SUCCESS.equals(thanhToan.getTrangThai())) {
            thanhToan.setTrangThai(paymentStatus);
        }
        thanhToan.setThoiGian(LocalDateTime.now(VN_ZONE));
        thanhToanRepository.save(thanhToan);

        if (TrangThaiThanhToan.SUCCESS.equals(thanhToan.getTrangThai())) {
            donHang.setTrangThai(OrderStatus.PAID);
            donHangRepository.save(donHang);
        }

        vnPay.setVnpTransactionNo(responseParams.get("vnp_TransactionNo"));
        vnPay.setVnpTransactionDate(responseParams.get("vnp_PayDate"));
        vnPay.setVnpResponseCode(responseCode);
        vnPay.setVnpBankCode(responseParams.get("vnp_BankCode"));
        vnPay.setVnpSecureHash(responseParams.get("vnp_SecureHash"));
        vnPay.setRawResponse(new TreeMap<>(responseParams));
        vnPay.setThoiGian(LocalDateTime.now(VN_ZONE));
        thanhToanVNPayRepository.save(vnPay);

        return new VNPayReturnResponse(
                thanhToan.getId(),
                donHang.getId(),
                txnRef,
                thanhToan.getTrangThai(),
                validSignature,
                responseCode,
                responseParams.get("vnp_TransactionNo"),
                responseParams.get("vnp_BankCode")
        );
    }

    private boolean verifySignature(Map<String, String> responseParams) {
        String receivedHash = responseParams.get("vnp_SecureHash");
        if (!StringUtils.hasText(receivedHash)) {
            return false;
        }

        Map<String, String> signedParams = new TreeMap<>();
        responseParams.forEach((key, value) -> {
            if (StringUtils.hasText(value)
                    && !"vnp_SecureHash".equals(key)
                    && !"vnp_SecureHashType".equals(key)) {
                signedParams.put(key, value);
            }
        });

        String hashData = buildHashData(signedParams);
        String calculatedHash = hmacSha512(requiredConfig(vnPayProperties.getHashSecret()), hashData);
        log.info(
                "VNPay return hashData={}, calculatedHash={}, receivedHash={}",
                hashData,
                calculatedHash,
                receivedHash
        );
        return calculatedHash.equalsIgnoreCase(receivedHash);
    }

    private String buildHashData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String key : fieldNames) {
            String value = params.get(key);
            if (StringUtils.hasText(value)) {
                if (!hashData.isEmpty()) {
                    hashData.append('&');
                }
                hashData.append(key).append('=').append(urlEncode(value));
            }
        }
        return hashData.toString();
    }

    private String buildQuery(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder query = new StringBuilder();
        for (String key : fieldNames) {
            String value = params.get(key);
            if (StringUtils.hasText(value)) {
                if (!query.isEmpty()) {
                    query.append('&');
                }
                query.append(urlEncode(key)).append('=').append(urlEncode(value));
            }
        }
        return query.toString();
    }

    private String hmacSha512(String secret, String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA512);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA512));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new AppException(ErrorCode.VNPAY_SIGNATURE_FAILED);
        }
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }

    private String requiredConfig(String value) {
        if (!StringUtils.hasText(value)) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        return value.trim();
    }

    private String buildTxnRef(String orderId) {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return orderId.replace("-", "").substring(0, Math.min(18, orderId.replace("-", "").length())) + suffix;
    }

    private String resolveLocale(CreateVNPayPaymentRequest request) {
        if (request != null && StringUtils.hasText(request.locale())) {
            String locale = request.locale().trim().toLowerCase();
            if ("vn".equals(locale) || "en".equals(locale)) {
                return locale;
            }
        }
        return vnPayProperties.getLocale();
    }

    private String normalizeOrderInfo(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        return DIACRITICS.matcher(normalized)
                .replaceAll("")
                .replaceAll("[^A-Za-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            return normalizeIp(forwardedFor.split(",")[0].trim());
        }

        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return normalizeIp(realIp.trim());
        }

        return normalizeIp(request.getRemoteAddr());
    }

    private String normalizeIp(String ipAddress) {
        return "0:0:0:0:0:0:0:1".equals(ipAddress) || "::1".equals(ipAddress)
                ? "127.0.0.1"
                : ipAddress;
    }
}
