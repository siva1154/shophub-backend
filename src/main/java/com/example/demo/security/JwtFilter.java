package com.example.demo.security;

import java.io.IOException;

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

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService
            userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException,
                   IOException {
    	
    

        String requestURI =
                request.getRequestURI();

        System.out.println(
                "REQUEST URI: "
                + requestURI
        );

        /* ==========================================
           PUBLIC ROUTES
        ========================================== */

        boolean isPublicRoute =

                requestURI.equals(
                        "/api/auth/login"
                )

                ||

                requestURI.equals(
                        "/api/auth/register" )

                ||

                requestURI.equals(
                        "/api/auth/forgot-password" )

                ||
                
                

                requestURI.equals(
                        "/api/auth/reset-password")

                ||

                requestURI.equals(
                        "/api/auth/reactivate"
                )

                ||

                requestURI.startsWith( "/oauth2" )
                
                ||
                requestURI.equals("/api/auth/verify-registration")
                
                ||
                requestURI.equals(  "/api/auth/resend-registration-otp")

                ||

                requestURI.startsWith(  "/login" )
                
                || requestURI.equals("/api/products")
                || (requestURI.startsWith("/api/product/") && request.getMethod().equals("GET"));

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