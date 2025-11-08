package com.ecommerce.ecommerce_site.controller.web;

import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.service.MailService;
import com.ecommerce.ecommerce_site.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class RegistrationController {

    @Autowired
    private MailService mailService;
    @Autowired
    private UserRegisterService userRegisterService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private final ConcurrentHashMap<String, List<String>> concurrentHashMap = new ConcurrentHashMap<>();

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam  String email, @RequestParam String password){
        if(userRegisterService.isUserExists(email)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User With This Email Exists");
        }
        String otp = "";
        try {
            otp = mailService.sendOtp(email);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
        }
        concurrentHashMap.put(email, Arrays.asList(otp, password));
        return ResponseEntity.ok("OTP Sent to " +email);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> registerUser(@RequestParam String otp, @RequestParam String email){
        List<String> data = concurrentHashMap.get(email);
        if (data == null) {
            return ResponseEntity.badRequest().body("Email not found or OTP expired");
        }
        System.out.println(data.get(0).equals(otp));
        if (!data.get(0).equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
        System.out.println("otp is valid");

        Users user = new Users();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(data.get(1)));
        return ResponseEntity.ok(userRegisterService.register(user));
    }
}
