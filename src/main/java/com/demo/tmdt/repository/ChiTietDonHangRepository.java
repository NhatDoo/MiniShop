package com.demo.tmdt.repository;

import com.demo.tmdt.model.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, String> {

    List<ChiTietDonHang> findByDonHangId(String donHangId);
}
