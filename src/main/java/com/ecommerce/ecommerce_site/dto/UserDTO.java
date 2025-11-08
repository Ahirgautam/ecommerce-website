package com.ecommerce.ecommerce_site.dto;

import com.ecommerce.ecommerce_site.model.Enums.Theme;
import com.ecommerce.ecommerce_site.model.Users;
import lombok.Getter;

@Getter
public class UserDTO {
    private int userId;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePic;
    private String gender;
    private String authProvider;
    private String roleName;
    private Theme theme;

    public UserDTO(Users user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.profilePic = user.getProfilePic();
        this.gender = user.getGender();
        this.authProvider = user.getAuth_provider();
        this.roleName = user.getRoles() != null ? user.getRoles().getRole() : null;
        this.theme = user.getThemePreference();
    }


}

