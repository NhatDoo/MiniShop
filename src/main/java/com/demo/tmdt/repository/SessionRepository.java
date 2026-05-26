package com.demo.tmdt.repository;

import com.demo.tmdt.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

    List<Session> findByUserId(String userId);
    List<Session> findByUserIdAndRevokedFalse(String userId);
    List<Session> findByExpiresAtBefore(LocalDateTime time);
    List<Session> findByUserIdAndRevokedFalseAndExpiresAtAfter(
            String userId,
            LocalDateTime now
    );
}
