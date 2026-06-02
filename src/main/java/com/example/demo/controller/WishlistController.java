package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.model.Wishlist;
import com.example.demo.repo.ProductRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.repo.WishlistRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistRepo wishlistRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    // ✅ Get wishlist
    @GetMapping
    public List<Wishlist> getWishlist(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return wishlistRepo.findByUserId(user.getId());
    }

    // ✅ Add to wishlist
    @PostMapping("/{productId}")
    public Wishlist addToWishlist(@PathVariable Integer productId, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // prevent duplicates
        if (wishlistRepo.findByUserIdAndProductId(user.getId(), productId).isPresent()) {
            throw new RuntimeException("Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        return wishlistRepo.save(wishlist);
    }

    // ✅ Remove from wishlist
    @DeleteMapping("/{productId}")
    public String removeFromWishlist(@PathVariable Integer productId, Authentication authentication){
        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wishlist wishlist = wishlistRepo.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));

        wishlistRepo.delete(wishlist);

        return "Removed from wishlist";
    }
}