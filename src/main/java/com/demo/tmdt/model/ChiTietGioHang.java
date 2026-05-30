package com.demo.tmdt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "\"ChiTietGioHang\"",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_chitietgiohang_cart_product",
                columnNames = {"\"gioHangId\"", "\"sanPhamId\""}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChiTietGioHang {

    @Id
    @Column(columnDefinition = "text")
    String id;

    @Column(name = "\"gioHangId\"", columnDefinition = "text", nullable = false)
    String gioHangId;

    @Column(name = "\"sanPhamId\"", columnDefinition = "text", nullable = false)
    String sanPhamId;

    @Column(name = "\"giaTien\"", nullable = false)
    Integer giaTien;

    @Column(name = "\"soLuong\"", nullable = false)
    Integer soLuong;
}
