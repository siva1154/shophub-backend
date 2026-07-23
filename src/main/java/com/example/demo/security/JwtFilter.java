package com.example.demo.security;

import java.io.IOException;

import com.example.demo.controller.AddressController;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter
        extends OncePerRequestFilter {

    private final AddressController addressController;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService
            userDetailsService;

    JwtFilter(AddressController addressController) {
        this.addressController = addressController;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException,
                   IOException {
    	
    

        String requestURI =request.getRequestURI();
        String method = request.getMethod();

        System.out.println("=== JWT FILTER ===");
        System.out.println("URI: " + requestURI);
        System.out.println("Method: " + method);
        System.out.println("Full URL: " + request.getRequestURL());
        System.out.println("Query String: " + request.getQueryString());

        /* ==========================================
           PUBLIC ROUTES
        ========================================== */

        boolean isPublicRoute =
                requestURI.equals("/api/auth/login")
                || requestURI.equals("/api/auth/register")
                || requestURI.equals("/api/auth/forgot-password")
                || requestURI.equals("/api/auth/reset-password")
                || requestURI.equals("/api/auth/reactivate")
                || requestURI.equals("/api/auth/verify-registration")
                || requestURI.equals("/api/auth/resend-registration-otp")
                || requestURI.startsWith("/oauth2")
                || requestURI.startsWith("/login")
                
                // ✅ FIXED: Use startsWith and check method more carefully
                || requestURI.startsWith("/api/products")  // catches /api/products and /api/products?search=...
                || (requestURI.startsWith("/api/product/") && "GET".equals(request.getMethod()));
        /* ==========================================
           SKIP ONLY PUBLIC ROUTES
        ========================================== */

        if (isPublicRoute) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        /* ==========================================
           GET AUTH HEADER
        ========================================== */

        String authHeader =
                request.getHeader(
                        "Authorization"
                );

        System.out.println(
                "Authorization Header: "
                + authHeader
        );

        /* ==========================================
           JWT VALIDATION
        ========================================== */

        if (

            authHeader != null

            &&

            authHeader.startsWith(
                    "Bearer "
            )
        ) {

            String token =
                    authHeader.substring(7);

            try {

                String email =
                        jwtUtil.extractEmail(
                                token
                        );

                System.out.println(
                        "Extracted Email: "
                        + email
                );

                if (

                    email != null

                    &&

                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            == null
                ) {

                    UserDetails userDetails =

                            userDetailsService
                                    .loadUserByUsername(
                                            email
                                    );

                    boolean isValid =

                            jwtUtil.validateToken(
                                    token,
                                    userDetails.getUsername()
                            );

                    if (isValid) {

                        UsernamePasswordAuthenticationToken
                                authentication =

                                new UsernamePasswordAuthenticationToken(

                                        userDetails,

                                        null,

                                        userDetails.getAuthorities()
                                );

                        authentication.setDetails(

                                new WebAuthenticationDetailsSource()
                                        .buildDetails(
                                                request
                                        )
                        );

                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(
                                        authentication
                                );

                        System.out.println(
                                "Authentication set for user: "
                                + email
                        );

                    }

                    else {

                        System.out.println(
                                "JWT validation failed"
                        );
                    }
                }

            }

            catch (Exception e) {

                System.out.println(
                    "JWT Error: " + e.getMessage()
                );

                response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
                );

                response.getWriter().write(
                    "Token expired or invalid"
                );

                return;
            }
        }

        /* ==========================================
           CONTINUE FILTER CHAIN
        ========================================== */

        filterChain.doFilter(
                request,
                response
        );
    }
}