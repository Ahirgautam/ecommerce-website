package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.model.Orders;
import com.ecommerce.ecommerce_site.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepo extends JpaRepository<Orders, Long> {

    List<Orders> findByUser(Users user);
}
