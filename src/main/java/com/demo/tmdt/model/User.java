package com.demo.tmdt.model;

import com.demo.tmdt.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class User {

    @Id
    @Column(columnDefinition = "text")
    String id;

    @Column(name = "mat_khau_hash", columnDefinition = "text")
    String matKhauHash;

    @Column(name = "so_dien_thoai", columnDefinition = "text", unique = true)
    String soDienThoai;

    @Column(columnDefinition = "text", unique = true)
    String email;

    @Column(columnDefinition = "text")
    String ten;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "public.\"Role\"")
    Role role;

    // nếu bạn có session relation
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    List<Session> sessions;
}
