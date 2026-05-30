package com.demo.tmdt.repository;

import com.demo.tmdt.model.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, String> {

    List<DonHang> findByUserIdAndDeletedAtIsNull(String userId);

    Optional<DonHang> findByIdAndUserIdAndDeletedAtIsNull(String id, String userId);
}
