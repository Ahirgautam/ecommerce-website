package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemsRepo extends JpaRepository<OrderItems, Long> {
}
