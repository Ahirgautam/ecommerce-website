package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.model.Customers;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomersRepo extends JpaRepository<Customers, Long> {
}
