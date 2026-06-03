package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ForgotPasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ResetPasswordRequest;

import com.example.demo.model.PasswordResetOtp;
import com.example.demo.model.RegistrationOtp;
import com.example.demo.model.User;
import com.example.demo.model.UserProfile;

import com.example.demo.repo.PasswordResetOtpRepo;
import com.example.demo.repo.RegistrationOtpRepo;
import com.example.demo.repo.UserProfileRepo;
import com.example.demo.repo.UserRepo;

import com.example.demo.security.JwtUtil;

import com.example.demo.service.EmailService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserProfileRepo userProfileRepo;

    @Autowired
    private PasswordResetOtpRepo passwordResetOtpRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RegistrationOtpRepo registrationOtpRepo;

    /* ==========================================
       REGISTER
    ========================================== */

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {

        if (userRepo.findByEmail(
                request.getEmail()
        ).isPresent()) {

            return ResponseEntity
                    .badRequest()
                    .body("Email already exists");
        }

        registrationOtpRepo.deleteByEmail(
                request.getEmail()
        );

        String otp = String.format(
                "%06d",
                new Random()
                        .nextInt(1000000)
        );

        RegistrationOtp registrationOtp =
                new RegistrationOtp();

        registrationOtp.setEmail(
                request.getEmail()
        );

        registrationOtp.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        registrationOtp.setOtp(otp);

        registrationOtp.setExpiryTime(
                LocalDateTime.now()
                        .plusMinutes(10)
        );

        registrationOtpRepo.save(
                registrationOtp
        );

        emailService.sendOtpEmail(
                request.getEmail(),
                otp
        );

        return ResponseEntity.ok(
                "OTP sent successfully"
        );
    }

    /* ==========================================
       LOGIN
    ========================================== */

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {

        /* NULL CHECK */

        if (request.getEmail() == null ||
            request.getPassword() == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                        "Email or password is null"
                    );
        }

        /* FIND USER */

        User user = userRepo.findByEmail(
                request.getEmail()
        ).orElseThrow(() ->

                new RuntimeException(
                        "User not found"
                )
        );

        /* ACCOUNT STATUS */

        if (!user.isActive()) {

            /* BANNED USER */

            if (user.isBanned()) {

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(
                            "Your account has been banned"
                        );
            }

            /* DEACTIVATED USER */

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                        "ACCOUNT_DEACTIVATED"
                    );
        }

        /* PASSWORD CHECK */

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            return ResponseEntity
                    .status(
                        HttpStatus.UNAUTHORIZED
                    )
                    .body(
                        "Invalid credentials"
                    );
        }

        /* GENERATE JWT */

        String token =
                jwtUtil.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        /* RESPONSE */

        Map<String, String> response =
                new HashMap<>();

        response.put("token", token);

        response.put("role", user.getRole());

        return ResponseEntity.ok(
                response
        );
    }

    /* ==========================================
       FORGOT PASSWORD
    ========================================== */

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody
            ForgotPasswordRequest request
    ) {

        /* FIND USER */

        User user = userRepo.findByEmail(
                request.getEmail()
        ).orElseThrow(() ->

                new RuntimeException(
                        "Email not registered"
                )
        );

        /* ACCOUNT DISABLED */

        if (!user.isActive()) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                        "Your account has been disabled"
                    );
        }

        /* DELETE OLD OTP */

        passwordResetOtpRepo.deleteByEmail(
                request.getEmail()
        );

        /* GENERATE OTP */

        String otp = String.format(
                "%06d",
                new Random()
                        .nextInt(1000000)
        );

        /* SAVE OTP */

        PasswordResetOtp resetOtp =
                new PasswordResetOtp();

        resetOtp.setEmail(
                request.getEmail()
        );

        resetOtp.setOtp(otp);

        resetOtp.setExpiryTime(
                LocalDateTime.now()
                        .plusMinutes(10)
        );

        passwordResetOtpRepo.save(
                resetOtp
        );

        /* SEND EMAIL */

        emailService.sendOtpEmail(
                request.getEmail(),
                otp
        );

        return ResponseEntity.ok(
                "OTP sent successfully to your email"
        );
    }

    /* ==========================================
       REACTIVATE ACCOUNT
    ========================================== */

    @PostMapping("/reactivate")
    public ResponseEntity<?> reactivateAccount(
            @RequestParam String email
    ) {

        User user = userRepo.findByEmail(
                email
        ).orElseThrow(() ->

                new RuntimeException(
                        "User not found"
                )
        );

        /* BLOCK BANNED USERS */

        if (user.isBanned()) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                        "Your account has been banned"
                    );
        }

        /* REACTIVATE */

        user.setActive(true);

        userRepo.save(user);

        return ResponseEntity.ok(
                "Account reactivated successfully"
        );
    }

    /* ==========================================
       DEACTIVATE ACCOUNT
    ========================================== */

    @PutMapping("/deactivate-account")
    public ResponseEntity<?> deactivateAccount(
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        User user = userRepo.findByEmail(
                email
        ).orElseThrow(() ->

                new RuntimeException(
                        "User not found"
                )
        );

        /* ADMIN CANNOT DEACTIVATE */

        if (user.getRole()
                .equals("ADMIN")) {

            return ResponseEntity
                    .badRequest()
                    .body(
                        "Admin account cannot be deactivated"
                    );
        }

        /* ALREADY DEACTIVATED */

        if (!user.isActive()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                        "Account already deactivated"
                    );
        }

        /* DEACTIVATE */

        user.setActive(false);

        userRepo.save(user);

        return ResponseEntity.ok(
                "Account deactivated successfully"
        );
    }

    /* ==========================================
       RESET PASSWORD
    ========================================== */

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody
            ResetPasswordRequest request
    ) {

        /* PASSWORD MATCH */

        if (!request.getNewPassword()
                .equals(
                    request.getConfirmPassword()
                )) {

            throw new RuntimeException(
                    "Passwords do not match"
            );
        }

        /* FIND OTP */

        PasswordResetOtp resetOtp =
                passwordResetOtpRepo
                        .findByEmailAndOtp(
                                request.getEmail(),
                                request.getOtp()
                        )
                        .orElseThrow(() ->

                                new RuntimeException(
                                        "Invalid OTP"
                                )
                        );

        /* OTP EXPIRED */

        if (resetOtp.getExpiryTime()
                .isBefore(
                        LocalDateTime.now()
                )) {

            throw new RuntimeException(
                    "OTP has expired"
            );
        }

        /* FIND USER */

        User user = userRepo.findByEmail(
                request.getEmail()
        ).orElseThrow(() ->

                new RuntimeException(
                        "User not found"
                )
        );

        /* ACCOUNT STATUS */

        if (!user.isActive()) {

            /* BANNED USER */

            if (user.isBanned()) {

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(
                            "Your account has been banned"
                        );
            }

            /* DEACTIVATED USER */

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                        "ACCOUNT_DEACTIVATED"
                    );
        }

        /* UPDATE PASSWORD */

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepo.save(user);

        /* DELETE OTP */

        passwordResetOtpRepo.deleteByEmail(
                request.getEmail()
        );

        return ResponseEntity.ok(
                "Password reset successfully"
        );
    }
    
    @PostMapping("/verify-registration")
    @Transactional
    public ResponseEntity<?> verifyRegistration(
            @RequestParam String email,
            @RequestParam String otp
    ) {

        RegistrationOtp registrationOtp =
                registrationOtpRepo
                        .findByEmailAndOtp( email,  otp )
                        .orElse(null);

                        if (registrationOtp == null) {

                            return ResponseEntity
                                    .badRequest()
                                    .body("Invalid OTP");
                        }
        if (registrationOtp.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            return ResponseEntity
                    .badRequest()
                    .body("OTP has expired");
        }

        User user = new User();  

        user.setEmail(
                registrationOtp.getEmail()
        );

        user.setPassword(
                registrationOtp.getPassword()
        );

        user.setRole("USER");

        user.setActive(true);

        User savedUser =
                userRepo.save(user);

        UserProfile profile =
                new UserProfile();

        profile.setUser(savedUser);

        userProfileRepo.save(profile);

        registrationOtpRepo.delete(registrationOtp);

        return ResponseEntity.ok(
                "Email verified successfully. Account created."
        );
    }
    
    @PostMapping("/resend-registration-otp")
    public ResponseEntity<?> resendRegistrationOtp(
            @RequestParam String email
    ) {

        RegistrationOtp registrationOtp =
                registrationOtpRepo
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No pending registration found"
                                ));

        String otp = String.format(
                "%06d",
                new Random().nextInt(1000000)
        );

        registrationOtp.setOtp(otp);

        registrationOtp.setExpiryTime(
                LocalDateTime.now().plusMinutes(10)
        );

        registrationOtpRepo.save(registrationOtp);

        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok(
                "OTP resent successfully"
        );
    }
    
    @GetMapping("/test-mail")
    public String testMail() {

        emailService.sendEmail(
            "your-email@gmail.com",
            "Test",
            "Brevo test"
        );

        return "Mail Sent";
    }
}