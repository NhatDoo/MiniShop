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
        name = "\"GioHang\"",
        uniqueConstraints = @UniqueConstraint(name = "uk_giohang_user_id", columnNames = "\"userId\"")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GioHang {

    @Id
    @Column(columnDefinition = "text")
    String id;

    @Column(name = "\"userId\"", columnDefinition = "text", nullable = false, unique = true)
    String userId;
}
