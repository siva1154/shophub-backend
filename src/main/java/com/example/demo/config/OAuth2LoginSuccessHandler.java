package com.example.demo.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.stereotype.Component;

import com.example.demo.model.User;
import com.example.demo.model.UserProfile;

import com.example.demo.repo.UserRepo;
import com.example.demo.repo.UserProfileRepo;

import com.example.demo.security.JwtUtil;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler
        implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserProfileRepo userProfileRepo;

    @Override
    public void onAuthenticationSuccess(

            HttpServletRequest request,

            HttpServletResponse response,

            Authentication authentication

    )
            throws IOException,
                   ServletException {

        /* =========================
           GOOGLE USER
        ========================= */

        OAuth2User oauthUser =

                (OAuth2User)
                        authentication.getPrincipal();

        String email =
                oauthUser.getAttribute(
                        "email"
                );

        String name =
                oauthUser.getAttribute(
                        "name"
                );

        Optional<User> existingUser =

                userRepo.findByEmail(
                        email
                );

        User user;

        /* =========================
           USER EXISTS
        ========================= */

        if (existingUser.isPresent()) {

            user = existingUser.get();

            /* =====================
               BANNED USER
            ===================== */

            if (user.isBanned()) {

                response.sendRedirect(

                    "http://localhost:5173/login?error=banned"
                );

                return;
            }

            /* =====================
               AUTO REACTIVATE
            ===================== */

            if (!user.isActive()) {

                user.setActive(true);

                userRepo.save(user);

                System.out.println(
                    "User auto reactivated: "
                    + email
                );
            }

            /* =====================
               CREATE PROFILE IF MISSING
            ===================== */

            Optional<UserProfile> profile =

                    userProfileRepo.findByUserId(
                            user.getId()
                    );

            if (profile.isEmpty()) {

                UserProfile newProfile =
                        new UserProfile();

                newProfile.setUser(user);

                userProfileRepo.save(
                        newProfile
                );

                System.out.println(
                    "Missing profile created for: "
                    + email
                );
            }
        }

        /* =========================
           CREATE NEW USER
        ========================= */

        else {

            user = new User();

            user.setName(name);

            user.setEmail(email);

            user.setPassword(
                    "GOOGLE_LOGIN"
            );

            user.setRole("USER");

            user.setActive(true);

            user.setBanned(false);

            /* SAVE USER */

            User savedUser =
                    userRepo.save(user);

            /* CREATE PROFILE */

            UserProfile profile =
                    new UserProfile();

            profile.setUser(savedUser);

            userProfileRepo.save(profile);

            user = savedUser;

            System.out.println(
                "New Google user created: "
                + email
            );
        }

        /* =========================
           GENERATE JWT
        ========================= */

        String token =

                jwtUtil.generateToken(

                        user.getEmail(),

                        user.getRole()
                );

        System.out.println(
            "OAuth JWT Generated: "
            + token
        );

        /* =========================
           REDIRECT FRONTEND
        ========================= */

        response.sendRedirect(

            "http://localhost:5173/oauth-success"

            + "?token=" + token

            + "&role=" + user.getRole()
        );
    }
}