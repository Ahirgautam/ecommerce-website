package com.ecommerce.ecommerce_site.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public String sendOtp(String toEmail) throws RuntimeException{
        String otp = generateOtp();
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Sign Up Varification OTP");
            message.setText("Your OTP is : " + otp);
            mailSender.send(message);
            System.out.println("email is sent");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return otp;
    }
    private String generateOtp(){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
