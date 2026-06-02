package com.example.demo.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.JwtFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /* ==========================================
       JWT FILTER
    ========================================== */

    @Autowired
    private JwtFilter jwtFilter;

    /* ==========================================
       OAUTH2 SUCCESS HANDLER
    ========================================== */

    @Autowired
    private OAuth2LoginSuccessHandler
            oAuth2LoginSuccessHandler;

    /* ==========================================
       SECURITY FILTER CHAIN
    ========================================== */

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http

            /* =========================
               CSRF
            ========================= */

            .csrf(csrf -> csrf.disable())

            /* =========================
               CORS
            ========================= */

            .cors(Customizer.withDefaults())

            /* =========================
               SESSION POLICY
            ========================= */

            .sessionManagement(session ->

                session.sessionCreationPolicy(

                    SessionCreationPolicy.STATELESS
                )
            )

            
            /* =========================
               AUTHORIZATION
            ========================= */

            .authorizeHttpRequests(auth -> auth

                /* =====================
                   PUBLIC AUTH APIs
                ===================== */

       
                .requestMatchers(

                    "/api/auth/login",

                    "/api/auth/register",

                    "/api/auth/forgot-password",
                    
                    "/api/auth/verify-registration",
                    
                    "/api/auth/resend-registration-otp",

                    "/api/auth/reset-password",

                    "/api/auth/reactivate",

                    "/oauth2/**",

                    "/login/**"

                ).permitAll()

                /* =====================
                   AUTHENTICATED AUTH APIs
                ===================== */

                .requestMatchers(

                    "/api/auth/deactivate-account"

                ).authenticated()

                /* =====================
                   PUBLIC PRODUCT APIs
                ===================== */

                .requestMatchers(

                    HttpMethod.GET,

                    "/api/products"

                ).permitAll()

                .requestMatchers(

                    HttpMethod.GET,

                    "/api/product/**"

                ).permitAll()

                /* =====================
                   AUTHENTICATED USER APIs
                ===================== */

                .requestMatchers(

                    "/api/profile/**"

                ).authenticated()

                .requestMatchers(

                    "/api/addresses/**"

                ).authenticated()

                .requestMatchers(

                    "/api/wishlist/**"

                ).authenticated()

                .requestMatchers(

                    "/api/orders/**"

                ).authenticated()

                .requestMatchers(

                    "/api/payment/**"

                ).authenticated()

                .requestMatchers(

                    "/api/settings/**"

                ).authenticated()

                /* =====================
                   ADMIN APIs
                ===================== */

                .requestMatchers(

                    "/api/admin/**"

                ).hasRole("ADMIN")

                /* =====================
                   PRODUCT MANAGEMENT
                ===================== */

                .requestMatchers(

                    HttpMethod.POST,

                    "/api/product/**"

                ).authenticated()

                .requestMatchers(

                    HttpMethod.PUT,

                    "/api/product/**"

                ).authenticated()

                .requestMatchers(

                    HttpMethod.DELETE,

                    "/api/product/**"

                ).authenticated()

                /* =====================
                   ALL OTHER APIs
                ===================== */

                .anyRequest().authenticated()
            )

            /* =========================
               OAUTH2 LOGIN
            ========================= */

            .oauth2Login(oauth -> oauth

                .successHandler(
                    oAuth2LoginSuccessHandler
                )
            );
        

        /* =========================
           JWT FILTER
        ========================= */

        http.addFilterBefore(

            jwtFilter,

            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    /* ==========================================
       CORS CONFIGURATION
    ========================================== */

    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(

            List.of(
                "http://localhost:5173"
            )
        );

        configuration.setAllowedMethods(

            List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(

            List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(

            "/**",

            configuration
        );

        return source;
    }

    /* ==========================================
       PASSWORD ENCODER
    ========================================== */

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}