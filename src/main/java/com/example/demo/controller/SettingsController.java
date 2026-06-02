package com.example.demo.controller;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.NotificationSettingsRequest;
import com.example.demo.model.User;
import com.example.demo.model.UserProfile;
import com.example.demo.repo.UserProfileRepo;
import com.example.demo.repo.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin
public class SettingsController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserProfileRepo userProfileRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* =========================
       CHANGE PASSWORD
    ========================= */
    @PutMapping("/change-password")
    public String changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepo.save(user);

        return "Password changed successfully";
    }

    /* =========================
       GET NOTIFICATION SETTINGS
    ========================= */
    @GetMapping("/notifications")
    public NotificationSettingsRequest getNotifications(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        NotificationSettingsRequest dto =
                new NotificationSettingsRequest();

        dto.setOrderNotifications(
                profile.getOrderNotifications());

        dto.setPromotionalEmails(
                profile.getPromotionalEmails());

        return dto;
    }

    /* =========================
       UPDATE NOTIFICATION SETTINGS
    ========================= */
    @PutMapping("/notifications")
    public String updateNotifications(
            @RequestBody NotificationSettingsRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setOrderNotifications(
                request.getOrderNotifications());

        profile.setPromotionalEmails(
                request.getPromotionalEmails());

        userProfileRepo.save(profile);

        return "Notification settings updated";
    }

    /* =========================
       DELETE ACCOUNT
    ========================= */
    @DeleteMapping("/delete-account")
    public String deleteAccount(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepo.delete(user);

        return "Account deleted successfully";
    }
}