package com.example.bank_backend.repository;

import com.example.bank_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
