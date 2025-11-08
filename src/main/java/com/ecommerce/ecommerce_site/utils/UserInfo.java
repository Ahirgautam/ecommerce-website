package com.ecommerce.ecommerce_site.utils;

import com.ecommerce.ecommerce_site.model.UserPrincipal;
import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

@Component
public class UserInfo {

    private final UserRegisterService userRegisterService;

    public UserInfo(UserRegisterService userRegisterService) {
        this.userRegisterService = userRegisterService;
    }


    public Users getUser(Object principal) {
        Users users = null;
        if (principal instanceof DefaultOidcUser oidcUser) {
            String email = oidcUser.getAttribute("email");
            users = userRegisterService.findUserByEmail(email);
        } else if (principal instanceof UserPrincipal userPrincipal) {
            users = userPrincipal.getUser();
        }
        return users;
    }


}
