package com.example.demo.config;

import com.razorpay.RazorpayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    private static final String KEY = "rzp_test_SaZ03zzfoQqnp3"; // replace
    private static final String SECRET = "B3sI4t2IZ0osZHiVtxoZtjwP";       // replace

    @Bean
    public RazorpayClient razorpayClient() throws Exception {
        return new RazorpayClient(KEY, SECRET);
    }
}