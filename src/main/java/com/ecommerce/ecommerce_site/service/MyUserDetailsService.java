package com.ecommerce.ecommerce_site.service;

import com.ecommerce.ecommerce_site.model.UserPrincipal;
import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
//creating MyUserDetailsService class that implements UserDetailsService
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    //overriding method from UserDetailsService that return user object if found else exception
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //get user by email
//        System.out.println(username + "this is username");
        Users user = repo.findByEmail(username);
        //if no user found with that email throw exception
        if(user == null){
            throw  new UsernameNotFoundException("User Not Found");
        }
        // return userPrincipal class object if found
        return new UserPrincipal(user);
    }
}
