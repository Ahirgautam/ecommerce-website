package com.ecommerce.ecommerce_site.service;

import com.ecommerce.ecommerce_site.dto.UserDTO;
import com.ecommerce.ecommerce_site.model.Enums.Theme;
import com.ecommerce.ecommerce_site.model.UserPrincipal;
import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserRegisterService userRegisterService;
    public List<UserDTO> getUsers(){
            List<Users> users = userRepo.findAll();
            List<UserDTO> userDTOS = new ArrayList<>();
            for(Users user : users){
                userDTOS.add(new UserDTO(user));
            }
            return userDTOS;
    }

    public Users getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = null;

        if (principal instanceof DefaultOidcUser oidcUser) {
            String email = oidcUser.getAttribute("email");
            users = userRegisterService.findUserByEmail(email);
        } else if (principal instanceof UserPrincipal userPrincipal) {
            users = userPrincipal.getUser();
        }

        return users;
    }

    public void changeTheme(String type) throws Exception{
        Users users = getCurrentUser();
        if(type.equals("light")){
            users.setThemePreference(Theme.LIGHT);
        }
        else if(type.equals("dark")){
            users.setThemePreference(Theme.DARK);
        }
        else{
            users.setThemePreference(Theme.SYSTEM);
        }
        userRepo.save(users);
    }
}
