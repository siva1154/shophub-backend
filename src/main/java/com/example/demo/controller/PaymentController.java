package com.example.demo.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin
public class PaymentController {

    @Autowired
    private RazorpayClient razorpayClient;

    @PostMapping("/create-order")
    public String createOrder(@RequestParam Double amount) throws Exception {

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100); // in paise
        options.put("currency", "INR");
        options.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(options);

        return order.toJson().toString();
    }
    }