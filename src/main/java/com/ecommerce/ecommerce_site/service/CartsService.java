package com.ecommerce.ecommerce_site.service;

import com.ecommerce.ecommerce_site.dto.CartItemsDTO;
import com.ecommerce.ecommerce_site.model.CartItems;
import com.ecommerce.ecommerce_site.model.Carts;
import com.ecommerce.ecommerce_site.model.Enums.CartStatus;
import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.repo.CartsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartsService {

    @Autowired
    private CartsRepo cartsRepo;

    @Autowired
    private CartItemsService cartItemsService;

    public Carts getCartByUser(Users users){
        return cartsRepo.findByUserAndCartStatus(users, CartStatus.ACTIVE).orElseGet(()-> null);
    }
    public Carts getCartByCartToken(String cartToken){
        return cartsRepo.findByCartTokenAndCartStatus(cartToken, CartStatus.ACTIVE).orElseGet(()-> null);
    }

    public Carts getOrCreateCartForUser(Users users){
        return cartsRepo.findByUserAndCartStatus(users, CartStatus.ACTIVE).orElseGet(()->{
            Carts carts = new Carts();
            carts.setUser(users);
            return cartsRepo.save(carts);

        });
    }

    public Carts getOrCreateCartByToken(String cartToken){
        return cartsRepo.findByCartTokenAndCartStatus(cartToken,  CartStatus.ACTIVE).orElseGet(()->{
            Carts carts = new Carts();
            carts.setCartToken(cartToken);
            return cartsRepo.save(carts);

        });
    }

    public Carts addProductToCart(Carts carts, long productId, long productVariantId, int quantity){
        cartItemsService.createAndSaveCartItem(carts, productId, productVariantId, quantity);

        return  carts;
    }


}
