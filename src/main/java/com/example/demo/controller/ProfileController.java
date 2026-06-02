package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.User;
import com.example.demo.model.UserProfile;
import com.example.demo.repo.UserProfileRepo;
import com.example.demo.repo.UserRepo;

@RestController
@CrossOrigin
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserProfileRepo userProfileRepo;

    // ✅ GET PROFILE (AUTO CREATE IF NOT EXISTS)
    @GetMapping
    public UserProfile getProfile(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userProfileRepo.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    newProfile.setName("");
                    newProfile.setPhone("");
                    newProfile.setAddress("");
                    return userProfileRepo.save(newProfile);
                });
    }

    // ✅ UPDATE PROFILE (ALSO SAFE IF NOT EXISTS)
    @PutMapping
    public UserProfile updateProfile(@RequestBody UserProfile updatedProfile,
                                     Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepo.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setName(updatedProfile.getName());
        profile.setPhone(updatedProfile.getPhone());
        profile.setAddress(updatedProfile.getAddress());

        return userProfileRepo.save(profile);
    }
}