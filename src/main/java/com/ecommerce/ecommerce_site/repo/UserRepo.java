package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//creating user repo inorder to connect with database
public interface UserRepo extends JpaRepository<Users, Integer> {

    Users findByEmail(String email);
}
