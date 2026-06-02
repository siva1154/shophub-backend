package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.UserProfile;

public interface UserProfileRepo extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUserId(Long userId);

}