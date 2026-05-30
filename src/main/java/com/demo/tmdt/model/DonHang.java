package com.demo.tmdt.model;

import com.demo.tmdt.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"DonHang\"")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonHang {

    @Id
    @Column(columnDefinition = "text")
    String id;

    @Column(name = "\"userId\"", columnDefinition = "text", nullable = false)
    String userId;

    @Column(name = "\"tongTien\"", nullable = false)
    Integer tongTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"trangThai\"", columnDefinition = "text", nullable = false)
    OrderStatus trangThai;

    @Column(name = "\"deletedAt\"")
    LocalDateTime deletedAt;
}
