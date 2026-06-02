package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Coupon;
import com.example.demo.repo.CouponRepo;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin
public class CouponController {

    @Autowired
    private CouponRepo couponRepo;

    @GetMapping("/validate/{code}")
    public Coupon validateCoupon(
            @PathVariable String code
    ) {

        return couponRepo.findByCodeAndActiveTrue(code)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid coupon"
                        ));
    }
    @GetMapping("/active")
    public List<Coupon> getActiveCoupons() {

        return couponRepo.findAll()
                .stream()
                .filter(Coupon::isActive)
                .toList();
    }
}