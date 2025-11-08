package com.ecommerce.ecommerce_site.service;


import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.repo.RolesRepo;
import com.ecommerce.ecommerce_site.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRegisterService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RolesRepo rolesRepo;

    public Users register(Users user){
        user.setRoles(rolesRepo.findByRole("customer"));
        return userRepo.save(user);
    }
    public boolean isUserExists(String email){
        return userRepo.findByEmail(email) != null;
    }
    public boolean needsProfileUpdateFromGoogle(String email){
        Users user = userRepo.findByEmail(email);
        if(user == null){
            Users newUser =new Users();
            newUser.setEmail(email);
            newUser.setRoles(rolesRepo.findByRole("customer"));
            userRepo.save(newUser);
            return true;
        }
        String auth = user.getAuth_provider();
        return auth != null && auth.equals("local");
    }

    public Users updateProfileFromGoogle(String email, String fname, String lname, String pic, String auth) {
        Users user = userRepo.findByEmail(email);
        if(user == null) user = new Users();
        if(user.getRoles() == null){
            user.setRoles(rolesRepo.findByRole("customer"));
        }
        user.setFirstName(fname);
        user.setLastName(lname);
        user.setProfilePic(pic);
        user.setAuth_provider(auth);
        return userRepo.save(user);
    }
    public Users findUserByEmail(String email){
        return userRepo.findByEmail(email);
    }
}
