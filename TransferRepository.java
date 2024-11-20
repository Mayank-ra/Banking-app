package com.example.bank_backend.repository;

import com.example.bank_backend.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByUserId(Long userId);
}
