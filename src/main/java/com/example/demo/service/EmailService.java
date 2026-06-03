package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    @Value("${BREVO_SENDER_EMAIL:noreply@shophub.com}")
    private String senderEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "ShopHub Password Reset OTP";
        String body = "Your ShopHub password reset OTP is: " + otp +
                "\n\nThis OTP will expire in 10 minutes." +
                "\n\nIf you did not request a password reset, please ignore this email.";
        sendEmail(toEmail, subject, body);
    }

    public void sendEmail(String toEmail, String subject, String body) {
        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> sender = new HashMap<>();
        sender.put("name", "ShopHub");
        sender.put("email", senderEmail);

        Map<String, Object> to = new HashMap<>();
        to.put("email", toEmail);

        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", sender);
        payload.put("to", List.of(to));
        payload.put("subject", subject);
        payload.put("textContent", body);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("Email sent to " + toEmail + ". Status: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email. Please try again later.", e);
        }
    }
}










//package com.example.demo.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendOtpEmail(String toEmail, String otp) {
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        message.setTo(toEmail);
//        message.setSubject("ShopHub Password Reset OTP");
//
//        message.setText(
//            "Your ShopHub password reset OTP is: " + otp +
//            "\n\nThis OTP will expire in 10 minutes." +
//            "\n\nIf you did not request a password reset, please ignore this email."
//        );
//
//        mailSender.send(message);
//    }
//    public void sendEmail(
//            String toEmail,
//            String subject,
//            String body
//    ) {
//
//        SimpleMailMessage message =  new SimpleMailMessage();
//
//        message.setTo(toEmail);
//
//        message.setSubject(subject);
//
//        message.setText(body);
//
//        mailSender.send(message);
//    }
//}