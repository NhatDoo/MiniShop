package com.demo.tmdt.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session {

    @Id
    @Column(columnDefinition = "text")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @Column(name = "token_hash", columnDefinition = "text")
    String tokenHash;

    @Column(name = "expires_at")
    LocalDateTime expiresAt;

    boolean revoked;

    @Column(name = "device_info", columnDefinition = "text")
    String deviceInfo;

    @Column(name = "last_used_at")
    LocalDateTime lastUsedAt;

    boolean rememberMe;
}
