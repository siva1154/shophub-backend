package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.example.demo.model.RegistrationOtp;

import jakarta.transaction.Transactional;

@Repository
public interface RegistrationOtpRepo
        extends JpaRepository<RegistrationOtp, Long> {

    Optional<RegistrationOtp>
        findByEmailAndOtp(
            String email,
            String otp
        );
    @Transactional
    @Modifying
    void deleteByEmail(String email);
    
    Optional<RegistrationOtp> findByEmail(
            String email
    );
}