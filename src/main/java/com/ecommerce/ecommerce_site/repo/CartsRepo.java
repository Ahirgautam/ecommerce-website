package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.model.Carts;
import com.ecommerce.ecommerce_site.model.Enums.CartStatus;
import com.ecommerce.ecommerce_site.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartsRepo extends JpaRepository<Carts, Long> {
    List<Carts> findByUser(Users user);
    List<Carts> findByCartToken(String cartToken);

    Optional<Carts> findByUserAndCartStatus(Users users, CartStatus cartStatus);
    Optional<Carts> findByCartTokenAndCartStatus(String cartToken, CartStatus cartStatus);


}
