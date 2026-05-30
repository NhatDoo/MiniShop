package com.demo.tmdt.repository;

import com.demo.tmdt.model.SanPham;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, String> {

    List<SanPham> findByDeletedAtIsNull();

    Optional<SanPham> findByIdAndDeletedAtIsNull(String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sp from SanPham sp where sp.id = :id and sp.deletedAt is null")
    Optional<SanPham> findWithLockByIdAndDeletedAtIsNull(@Param("id") String id);
}
