package com.demo.tmdt.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "vnpay")
public class VNPayProperties {

    private String tmnCode;
    private String hashSecret;
    private String paymentUrl;
    private String returnUrl;
    private String version = "2.1.0";
    private String command = "pay";
    private String currCode = "VND";
    private String orderType = "other";
    private String locale = "vn";
    private long expireMinutes = 15;
}
