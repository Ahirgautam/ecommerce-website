package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepo extends JpaRepository<Roles, Long> {
    Roles findByRole(String role);
}
