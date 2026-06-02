package com.example.demo.controller;

import java.util.HashMap;
import com.example.demo.model.UserProfile;
import com.example.demo.repo.UserProfileRepo;
import com.example.demo.service.EmailService;
import java.util.List;
import java.util.Map;

import com.example.demo.model.Coupon;
import com.example.demo.model.Order;
import com.example.demo.repo.CouponRepo;
import com.example.demo.repo.OrderRepo;
import com.example.demo.model.User;
import com.example.demo.dto.PromotionRequest;
import com.example.demo.model.UserProfile;
import com.example.demo.repo.UserProfileRepo;
import com.example.demo.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Product;
import com.example.demo.repo.ProductRepo;
import com.example.demo.repo.UserRepo;

@CrossOrigin
@RestController
@RequestMapping("/api/admin")
public class AdminContoller {

	@Autowired
	private OrderRepo orderRepo;
	
    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private UserProfileRepo userProfileRepo;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private CouponRepo couponRepo;
    
    

    @GetMapping("/stats")
    public Map<String, Object> getStats() {

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalProducts", productRepo.count());
        stats.put("totalUsers", userRepo.count());

        int totalStock = productRepo.findAll()
                .stream()
                .mapToInt(Product::getStockQuantity)
                .sum();

        stats.put("totalStock", totalStock);

        List<Product> latestProducts =
                productRepo.findTop5ByOrderByIdDesc();

        stats.put("latestProducts", latestProducts);

        return stats;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }
    
    @PutMapping("/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Order order = orderRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        order.setStatus(status);

        orderRepo.save(order);

        User user = order.getUser();

        UserProfile profile =
                userProfileRepo.findByUserId(user.getId())
                .orElse(null);

        if (profile != null &&
            Boolean.TRUE.equals(
                    profile.getOrderNotifications()
            )) {

            String subject =
                    "ShopHub Order Update";

            String body =
                    "Hello " + user.getName()

                    + "\n\nYour order #"

                    + order.getId()

                    + " status has been updated."

                    + "\n\nCurrent Status: "

                    + status

                    + "\n\nThank you for shopping with ShopHub.";

            emailService.sendEmail(
                    user.getEmail(),
                    subject,
                    body
            );
        }

        return "Order status updated successfully";
    }
    
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepo.findByActiveTrue();
    }
    
    @PutMapping("/users/{id}/role")
    public String updateUserRole(
            @PathVariable Long id,
            @RequestParam String role) {

        User user = userRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setRole(role);
        userRepo.save(user);

        return "User role updated successfully";
    }
    
    @PostMapping("/promotions/send")
    public String sendPromotion(
            @RequestBody PromotionRequest request
    ) {

    
        List<UserProfile> profiles =
                userProfileRepo.findAll();

        int sentCount = 0;

        for (UserProfile profile : profiles) {

            User user = profile.getUser();

            if (
                user != null &&
                user.isActive() &&
                Boolean.TRUE.equals(
                    profile.getPromotionalEmails()
                )
            ) {

                try {

                    emailService.sendEmail(
                            user.getEmail(),
                            request.getSubject(),
                            request.getMessage()
                    );

                    sentCount++;

                } catch (Exception e) {

                    System.out.println(
                        "Failed to send email to: "
                        + user.getEmail()
                    );

                    e.printStackTrace();
                }
            }
        }

        return "Promotion sent to "
                + sentCount
                + " users";
    }
    
//    @DeleteMapping("/users/{id}")
//    public String deleteUser(
//            @PathVariable Long id
//    ) {
//
//        User user = userRepo.findById(id)
//                .orElseThrow(() ->
//                        new RuntimeException(
//                                "User not found"
//                        ));
//
//        /* SOFT DELETE */
//        user.setActive(false);
//
//        userRepo.save(user);
//
//        return "User disabled successfully";
//    }
    
    @PostMapping("/coupons")
    public Coupon createCoupon(
            @RequestBody Coupon coupon
    ) {
        return couponRepo.save(coupon);
    }
    
    @GetMapping("/coupons")
    public List<Coupon> getCoupons() {
        return couponRepo.findAll();
    }
    
    @PutMapping("/coupons/{id}/toggle")
    public Coupon toggleCoupon(
            @PathVariable Long id
    ) {

        Coupon coupon = couponRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Coupon not found"
                        ));

        coupon.setActive(
                !coupon.isActive()
        );

        return couponRepo.save(coupon);
    }
}