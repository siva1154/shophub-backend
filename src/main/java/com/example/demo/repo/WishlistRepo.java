package com.example.demo.repo;

import com.example.demo.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepo extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserId(Long userId);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Integer productId);
}