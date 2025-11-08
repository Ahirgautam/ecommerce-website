package com.ecommerce.ecommerce_site.controller.api;

import com.ecommerce.ecommerce_site.dto.UserDTO;
import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/api/users")
    public List<UserDTO> getUsers(){
        return userService.getUsers();
    }

    @PostMapping("/api/users/theme")
    public ResponseEntity<?> changeTheme(@RequestBody Map<String,String> map){
        try{
            userService.changeTheme(map.get("theme"));
            return ResponseEntity.ok("theme is changed");
        }
        catch (Exception e){
            System.out.println("Error " + e);
            return ResponseEntity.badRequest().build();
        }
    }
}
