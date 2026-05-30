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

import java.time.LocalDateTime;

@Entity
@Table(name = "sanpham")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SanPham {

    @Id
    @Column(columnDefinition = "text")
    String id;

    @Column(columnDefinition = "text")
    String ten;

    @Column(name = "\"moTa\"", columnDefinition = "text")
    String moTa;

    @Column(name = "\"giaTien\"")
    Integer giaTien;

    @Column(name = "\"soLuongTon\"")
    Integer soLuongTon;

    @Column(name = "\"deletedAt\"")
    LocalDateTime deletedAt;

    @Column(name = "\"hinhanhUrl\"", columnDefinition = "text")
    String hinhanhUrl;
}
