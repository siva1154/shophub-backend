package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Coupon;

public interface CouponRepo
        extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeAndActiveTrue(
            String code
    );
}