package com.ecommerce.ecommerce_site.model;

import com.ecommerce.ecommerce_site.model.Enums.Theme;
import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
// orm(object relation mapping) mapping user class with user table
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    @Column(nullable = false, unique = true)
    private String email;
    @JsonIgnore
    private String password;
    private String firstName;
    private String lastName;
    private String profilePic;
    private String gender;
    private String auth_provider = "local";

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Carts> carts;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Orders> orders ;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<LikedProducts> likedProducts = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonBackReference
    private Roles role;

    @Enumerated(EnumType.STRING)
    private Theme themePreference = Theme.DARK;

    public Theme getThemePreference() {
        return themePreference;
    }

    public void setThemePreference(Theme themePreference) {
        this.themePreference = themePreference;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getAuth_provider() {
        return auth_provider;
    }

    public void setAuth_provider(String auth_provider) {
        this.auth_provider = auth_provider;
    }

    public String getFullName(){
        if(firstName == null && lastName == null) return null;
        return firstName + " " + lastName;
    }

    public List<Carts> getCarts() {
        return carts;
    }

    public void setCarts(List<Carts> carts) {
        this.carts = carts;
    }

    public Roles getRoles() {
        return role;
    }

    public void setRoles(Roles role) {
        this.role = role;
    }

   public String getRole(){
        return  this.role.getRole();
   }
}
