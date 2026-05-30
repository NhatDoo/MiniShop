package com.demo.tmdt.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1000, "Invalid request", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1001, "Email is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1002, "Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(1004, "Phone number is invalid", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1005, "Email already exists", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1007, "Invalid refresh token", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(1008, "User not found", HttpStatus.NOT_FOUND),
    SESSION_NOT_FOUND(1009, "Session not found", HttpStatus.NOT_FOUND),
    WRONG_PASSWORD(1010, "Wrong password", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN(1011, "Invalid access token", HttpStatus.UNAUTHORIZED),
    INVALID_GOOGLE_ID_TOKEN(1012, "Invalid Google ID token", HttpStatus.UNAUTHORIZED),
    SANPHAM_NOT_FOUND(1013, "Product not found", HttpStatus.NOT_FOUND),
    SANPHAM_NAME_INVALID(1014, "Product name is required", HttpStatus.BAD_REQUEST),
    SANPHAM_PRICE_INVALID(1015, "Product price must be greater than or equal to 0", HttpStatus.BAD_REQUEST),
    SANPHAM_QUANTITY_INVALID(1016, "Product quantity must be greater than or equal to 0", HttpStatus.BAD_REQUEST),
    MINIO_UPLOAD_FAILED(1017, "Image upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TOO_LARGE(1018, "Uploaded file is too large", HttpStatus.PAYLOAD_TOO_LARGE),
    CART_NOT_FOUND(1019, "Cart not found", HttpStatus.NOT_FOUND),
    PRODUCT_OUT_OF_STOCK(1020, "Product is out of stock", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY(1021, "Quantity must be greater than 0", HttpStatus.BAD_REQUEST),
    ORDER_CREATION_FAILED(1022, "Order creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED(1023, "Access denied", HttpStatus.FORBIDDEN),
    ORDER_NOT_FOUND(1024, "Order not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_PAYABLE(1025, "Order is not payable", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_FOUND(1026, "Payment not found", HttpStatus.NOT_FOUND),
    INVALID_VNPAY_RESPONSE(1027, "Invalid VNPay response", HttpStatus.BAD_REQUEST),
    VNPAY_SIGNATURE_FAILED(1028, "VNPay signature failed", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
