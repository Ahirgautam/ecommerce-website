package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.model.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingAddressRepo extends JpaRepository<ShippingAddress, Long> {
}
