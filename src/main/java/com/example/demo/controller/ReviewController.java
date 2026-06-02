package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ReviewRequest;
import com.example.demo.model.Product;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.repo.ProductRepo;
import com.example.demo.repo.ReviewRepo;
import com.example.demo.repo.UserRepo;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @PostMapping
    public String addReview(
            @RequestBody ReviewRequest request,
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        User user =
                userRepo.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        Product product =
                productRepo.findById(
                        request.getProductId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        ));

        boolean alreadyReviewed =
                reviewRepo.existsByUserIdAndProductId(
                        user.getId(),
                        product.getId()
                );

        if (alreadyReviewed) {

            throw new RuntimeException(
                    "You already reviewed this product"
            );
        }

        Review review =
                new Review();

        review.setUser(user);

        review.setProduct(product);

        review.setRating(
                request.getRating()
        );

        review.setComment(
                request.getComment()
        );

        reviewRepo.save(review);

        return "Review added successfully";
    }

    @GetMapping("/{productId}")
    public List<Review> getReviews(
            @PathVariable Long productId
    ) {

        return reviewRepo.findByProductId(
                productId
        );
    }
    
    @GetMapping("/{productId}/rating")
    public double getAverageRating(
            @PathVariable Long productId
    ) {

        List<Review> reviews =
                reviewRepo.findByProductId(
                        productId
                );

        if (reviews.isEmpty()) {

            return 0;
        }

        return reviews.stream()

                .mapToInt(
                        Review::getRating
                )

                .average()

                .orElse(0);
    }
    
    @GetMapping("/has-reviewed/{productId}")
    public boolean hasReviewed(
            @PathVariable int productId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return reviewRepo.existsByUserIdAndProductId(
                user.getId(),
                productId
        );
    }
}