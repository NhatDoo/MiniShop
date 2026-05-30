package com.demo.tmdt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "\"ThanhToanVNPay\"")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThanhToanVNPay {

    @Id
    @Column(columnDefinition = "text")
    String id;

    @Column(name = "\"thanhToanId\"", columnDefinition = "text", nullable = false)
    String thanhToanId;

    @Column(name = "\"vnpRequestId\"", columnDefinition = "text")
    String vnpRequestId;

    @Column(name = "\"vnpVersion\"", columnDefinition = "text")
    String vnpVersion;

    @Column(name = "\"vnpCommand\"", columnDefinition = "text")
    String vnpCommand;

    @Column(name = "\"vnpTmnCode\"", columnDefinition = "text")
    String vnpTmnCode;

    @Column(name = "\"vnpTxnRef\"", columnDefinition = "text", nullable = false)
    String vnpTxnRef;

    @Column(name = "\"vnpOrderInfo\"", columnDefinition = "text")
    String vnpOrderInfo;

    @Column(name = "\"vnpCreateDate\"", columnDefinition = "text")
    String vnpCreateDate;

    @Column(name = "\"vnpIpAddr\"", columnDefinition = "text")
    String vnpIpAddr;

    @Column(name = "\"vnpTransactionNo\"", columnDefinition = "text")
    String vnpTransactionNo;

    @Column(name = "\"vnpTransactionDate\"", columnDefinition = "text")
    String vnpTransactionDate;

    @Column(name = "\"vnpResponseCode\"", columnDefinition = "text")
    String vnpResponseCode;

    @Column(name = "\"vnpBankCode\"", columnDefinition = "text")
    String vnpBankCode;

    @Column(name = "\"vnpSecureHash\"", columnDefinition = "text")
    String vnpSecureHash;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"rawResponse\"", columnDefinition = "jsonb")
    Map<String, String> rawResponse;

    @Column(name = "\"thoiGian\"")
    LocalDateTime thoiGian;
}
