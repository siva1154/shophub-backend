package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Review;

public interface ReviewRepo
        extends JpaRepository<Review, Long> {

    List<Review> findByProductId(Long productId);

    boolean existsByUserIdAndProductId(
            Long userId,
            int productId
    );
}