package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.PasswordResetOtp;

import jakarta.transaction.Transactional;

public interface PasswordResetOtpRepo
        extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findByEmail(String email);

    Optional<PasswordResetOtp> findByEmailAndOtp(
            String email,
            String otp
    );

    @Transactional
    void deleteByEmail(String email);
}