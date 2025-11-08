package com.ecommerce.ecommerce_site.controller.api;

import com.ecommerce.ecommerce_site.model.Carts;
import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.repo.CartItemsRepo;
import com.ecommerce.ecommerce_site.repo.CartsRepo;
import com.ecommerce.ecommerce_site.service.CartItemsService;
import com.ecommerce.ecommerce_site.service.CartsService;
import com.ecommerce.ecommerce_site.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
public class CartsController {

    @Autowired
    private CartsService cartsService;

    @Autowired
    private CartsRepo cartsRepo;
    @Autowired
    private UserService userService;

    @Autowired
    private CartItemsRepo cartItemsRepo;

    @Autowired
    private CartItemsService cartItemsService;

    @GetMapping("/")
    public ResponseEntity<?> getAllCartItems(HttpServletRequest request, HttpServletResponse response){
        Carts carts = getCurrentCart(request, response, false);
        if(carts == null){
            return new ResponseEntity<>("No Cart Found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(cartItemsRepo.fetchAllCartItems(carts.getId()));
    }
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
        @RequestParam Long productId,
        @RequestParam Long variantId,
        @RequestParam int quantity,
        HttpServletRequest request,
        HttpServletResponse response
    ){
        Carts carts = getCurrentCart(request,response, true);
        if(carts == null) return new ResponseEntity<>("no cart found", HttpStatus.NOT_FOUND);
        cartsService.addProductToCart(carts, productId, variantId, quantity);
        return ResponseEntity.ok("product added to cart");
    }

    @GetMapping("/count")
    public ResponseEntity<?> getProductCountInCart(HttpServletRequest request, HttpServletResponse response){
        Carts carts = getCurrentCart(request, response, false);
        if(carts == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carts.getCartItems().size());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long id){
        cartItemsService.deleteCartItem(id);
        return ResponseEntity.ok("cart item deleted");
    }

    @PostMapping("/{id}")
    public void updateCartItem(@PathVariable Long id, @RequestBody Map<String, Object> payload){
        Integer quantity = Integer.parseInt((String)payload.get("quantity"));
        cartItemsService.updateCartItem(id, quantity);
    }
    public Carts getCurrentCart(HttpServletRequest request, HttpServletResponse response, boolean createIfNotExists){
        Users currentUser = userService.getCurrentUser();
//        if(currentUser == null) return  null;
        Carts carts = null;
        if(currentUser != null){
            if(!createIfNotExists){
                carts = cartsService.getCartByUser(currentUser);
                return carts;
            }
            carts = cartsService.getOrCreateCartForUser(currentUser);
        }
        else{
            String cartToken = getCartTokenFromCookie(request);
            if(cartToken == null && createIfNotExists){
                cartToken = UUID.randomUUID().toString();
                Cookie cookie = new Cookie("cart_token", cartToken);
                cookie.setPath("/");
                cookie.setMaxAge(60*60*24*30);
                response.addCookie(cookie);
            }
            if(createIfNotExists)
                carts = cartsService.getOrCreateCartByToken(cartToken);
            else
                carts = cartsService.getCartByCartToken(cartToken);
        }
        return  carts;
    }
    public String getCartTokenFromCookie(HttpServletRequest request){
        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if("cart_token".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


}
