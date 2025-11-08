package com.ecommerce.ecommerce_site.service;

import com.ecommerce.ecommerce_site.dto.CartItemsDTO;
import com.ecommerce.ecommerce_site.model.CartItems;
import com.ecommerce.ecommerce_site.model.Carts;
import com.ecommerce.ecommerce_site.model.ProductVariants;
import com.ecommerce.ecommerce_site.model.Products;
import com.ecommerce.ecommerce_site.repo.CartItemsRepo;
import com.ecommerce.ecommerce_site.repo.ProductVariantsRepo;
import com.ecommerce.ecommerce_site.repo.ProductsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemsService {
    @Autowired
    private ProductVariantsRepo productVariantsRepo;
    @Autowired
    private ProductsRepo productsRepo;
    @Autowired
    private CartItemsRepo cartItemsRepo;


    public CartItems createAndSaveCartItem(Carts carts, long productId, long variantId, int quantity){
        Products products = productsRepo.findById(productId).orElseThrow();
        ProductVariants productVariants = productVariantsRepo.findById(variantId).orElseThrow();
        Optional<CartItems> optionalCartItems = cartItemsRepo.findByProductsAndProductVariantsAndCart(products, productVariants, carts);
        if(optionalCartItems.isPresent()){

            int qnt = optionalCartItems.get().getQuantity()+quantity;
            if(qnt > productVariants.getStock()) qnt = (int)productVariants.getStock();
            optionalCartItems.get().setQuantity(qnt);
            return cartItemsRepo.save(optionalCartItems.get());
        }
        CartItems cartItems = new CartItems();

        cartItems.setCart(carts);
        cartItems.setProducts(products);
        cartItems.setProductVariants(productVariants);
        cartItems.setQuantity(quantity);
        cartItems.setSubTotal(cartItems.getProductVariants().getDiscountedPrice().multiply(new BigDecimal(quantity)));
        cartItems.setPriceAtAddition(cartItems.getProductVariants().getDiscountedPrice());
        cartItemsRepo.save(cartItems);
        return  cartItems;
    }

    public List<CartItemsDTO> getAllItems(Carts carts){
        return cartItemsRepo.fetchAllCartItems(carts.getId());
    }

    public void deleteCartItem(Long id){
        if(cartItemsRepo.findById(id).isPresent())
            cartItemsRepo.deleteById(id);
    }
    public void updateCartItem(Long id, Integer qt){
        CartItems cartItems = cartItemsRepo.findById(id).get();
        cartItems.setQuantity(qt);
        cartItemsRepo.save(cartItems);
    }
}
