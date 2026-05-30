package com.demo.tmdt.repository;

import com.demo.tmdt.model.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, String> {

    Optional<GioHang> findByUserId(String userId);
}
