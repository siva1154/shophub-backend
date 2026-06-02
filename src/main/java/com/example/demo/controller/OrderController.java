package com.example.demo.controller;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.model.UserProfile;
import com.example.demo.repo.UserProfileRepo;
import com.example.demo.service.EmailService;
import com.example.demo.model.*;
import com.example.demo.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private AddressRepo addressRepo;
    
    @Autowired
    private UserProfileRepo userProfileRepo;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private CouponRepo couponRepo;

    @PostMapping
    public Order placeOrder(@RequestBody PlaceOrderRequest request,
                            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepo.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PLACED");
        order.setPaymentMethod(request.getPaymentMethod());

        // ✅ COD payment setup
        if ("COD".equalsIgnoreCase(request.getPaymentMethod())) {
            order.setPaymentStatus("COD");
        } else {
            order.setPaymentStatus("PENDING");
        }

        List<OrderItem> items = request.getCartItems().stream().map(cart -> {
            Product product = productRepo.findById(cart.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // ✅ Check stock before ordering
            if (product.getStockQuantity() < cart.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // ✅ Reduce stock
            product.setStockQuantity(product.getStockQuantity() - cart.getQuantity());
            productRepo.save(product);

            // ✅ Create order item
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(cart.getQuantity());
            item.setPrice(product.getPrice());
            item.setOrder(order);

            return item;
        }).toList();

        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal finalTotal = total;

        if (request.getCouponCode() != null &&
            !request.getCouponCode().isBlank()) {

            Coupon coupon = couponRepo
                    .findByCodeAndActiveTrue(
                            request.getCouponCode()
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Invalid coupon"
                            ));

            BigDecimal discount = total.multiply(
                    BigDecimal.valueOf(
                            coupon.getDiscountPercentage()
                    )
            ).divide(
                    BigDecimal.valueOf(100)
            );

            finalTotal = total.subtract(discount);
        }

        order.setItems(items);
        order.setTotalAmount(finalTotal);

        Order savedOrder = orderRepo.save(order);

        /* =========================
           ORDER EMAIL NOTIFICATION
        ========================= */

        UserProfile profile =
                userProfileRepo.findByUserId(user.getId())
                .orElse(null);

        if (profile != null &&
            Boolean.TRUE.equals(
                    profile.getOrderNotifications()
            )) {

            emailService.sendEmail(

                    user.getEmail(),

                    "ShopHub Order Confirmation",

                    "Hello " + user.getName() +

                    "\n\nYour order #" +
                    savedOrder.getId() +

                    " has been placed successfully." +

                    "\n\nTotal Amount: ₹" +
                    savedOrder.getTotalAmount() +

                    "\n\nPayment Method: " +
                    savedOrder.getPaymentMethod() +

                    "\n\nStatus: " +
                    savedOrder.getStatus() +

                    "\n\nThank you for shopping with ShopHub!"
            );
        }

        return savedOrder;
    }

    @GetMapping
    public List<Order> getOrders(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepo.findByUserId(user.getId());
    }
    
    
    @PutMapping("/cancel/{orderId}")
    public Order cancelOrder(@PathVariable Long orderId, Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ Ensure user can only cancel their own order
        if (order.getUser().getId()!=(user.getId())) {
            throw new RuntimeException("Unauthorized to cancel this order");
        }

        // ✅ Prevent cancelling already cancelled orders
        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Order already cancelled");
        }

        // ✅ Return stock back
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepo.save(product);
        }

        // ✅ Update order status
        order.setStatus("CANCELLED");

        Order cancelledOrder = orderRepo.save(order);

        /* =========================
           CANCELLATION EMAIL
        ========================= */

        UserProfile profile =
                userProfileRepo.findByUserId(user.getId())
                .orElse(null);

        if (profile != null &&
            Boolean.TRUE.equals(
                    profile.getOrderNotifications()
            )) {

            emailService.sendEmail(

                    user.getEmail(),

                    "ShopHub Order Cancelled",

                    "Hello " + user.getName()

                    + "\n\nYour order #"

                    + cancelledOrder.getId()

                    + " has been cancelled successfully."

                    + "\n\nRefund (if applicable) will be processed according to the payment method."

                    + "\n\nThank you for using ShopHub."
            );
        }

        return cancelledOrder;
    }
}