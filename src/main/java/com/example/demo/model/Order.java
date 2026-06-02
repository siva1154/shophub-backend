package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate;

    private BigDecimal totalAmount;

    private String status; // PLACED / CANCELLED / DELIVERED

    private String paymentMethod; // COD / RAZORPAY

    private String paymentStatus; // PENDING / PAID / FAILED / COD

    private String paymentId; // Razorpay payment id

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "address_id",nullable = true)
    private Address address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> items;

    public Long getId() {
        return id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public User getUser() {
        return user;
    }

    public Address getAddress() {
        return address;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}