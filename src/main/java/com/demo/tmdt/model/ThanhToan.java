package com.demo.tmdt.model;

import com.demo.tmdt.enums.TrangThaiThanhToan;
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
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"ThanhToan\"")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThanhToan {

    @Id
    @Column(columnDefinition = "text")
    String id;

    @Column(name = "\"donHangId\"", columnDefinition = "text", nullable = false)
    String donHangId;

    @Column(name = "\"tongTien\"", nullable = false)
    Integer tongTien;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @ColumnTransformer(write = "?::public.\"TrangThaiThanhToan\"")
    @Column(name = "\"trangThai\"", columnDefinition = "public.\"TrangThaiThanhToan\"", nullable = false)
    TrangThaiThanhToan trangThai;

    @Column(name = "\"phuongThuc\"", columnDefinition = "text", nullable = false)
    String phuongThuc;

    @Column(name = "\"thoiGian\"")
    LocalDateTime thoiGian;

    @Column(name = "\"deletedAt\"")
    LocalDateTime deletedAt;
}
