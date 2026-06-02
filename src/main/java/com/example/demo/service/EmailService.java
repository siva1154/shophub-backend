package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("ShopHub Password Reset OTP");

        message.setText(
            "Your ShopHub password reset OTP is: " + otp +
            "\n\nThis OTP will expire in 10 minutes." +
            "\n\nIf you did not request a password reset, please ignore this email."
        );

        mailSender.send(message);
    }
    public void sendEmail(
            String toEmail,
            String subject,
            String body
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(subject);

        message.setText(body);

        mailSender.send(message);
    }
}