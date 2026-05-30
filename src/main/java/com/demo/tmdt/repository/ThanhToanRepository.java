package com.demo.tmdt.repository;

import com.demo.tmdt.model.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, String> {

    List<ThanhToan> findByDonHangIdAndDeletedAtIsNull(String donHangId);
}
