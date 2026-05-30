package com.demo.tmdt.repository;

import com.demo.tmdt.model.ChiTietGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, String> {

    List<ChiTietGioHang> findByGioHangId(String gioHangId);

    Optional<ChiTietGioHang> findByIdAndGioHangId(String id, String gioHangId);

    Optional<ChiTietGioHang> findByGioHangIdAndSanPhamId(String gioHangId, String sanPhamId);

    void deleteByGioHangId(String gioHangId);
}
