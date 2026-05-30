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

@Entity
@Table(name = "\"ChiTietDonHang\"")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChiTietDonHang {

    @Id
    @Column(columnDefinition = "text")
    String id;

    @Column(name = "\"donHangId\"", columnDefinition = "text", nullable = false)
    String donHangId;

    @Column(name = "\"sanPhamId\"", columnDefinition = "text", nullable = false)
    String sanPhamId;

    @Column(name = "\"soLuong\"", nullable = false)
    Integer soLuong;

    @Column(name = "\"giaTien\"", nullable = false)
    Integer giaTien;
}
