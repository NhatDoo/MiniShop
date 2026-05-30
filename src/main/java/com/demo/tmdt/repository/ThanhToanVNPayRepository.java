package com.demo.tmdt.repository;

import com.demo.tmdt.model.ThanhToanVNPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThanhToanVNPayRepository extends JpaRepository<ThanhToanVNPay, String> {

    Optional<ThanhToanVNPay> findByVnpTxnRef(String vnpTxnRef);
}
