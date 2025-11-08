package com.ecommerce.ecommerce_site.controller.web;

import com.ecommerce.ecommerce_site.dto.CategoryDTO;
import com.ecommerce.ecommerce_site.dto.ProductDTO;
import com.ecommerce.ecommerce_site.model.*;
import com.ecommerce.ecommerce_site.model.Enums.CartStatus;
import com.ecommerce.ecommerce_site.repo.CartsRepo;
import com.ecommerce.ecommerce_site.service.CategoryService;
import com.ecommerce.ecommerce_site.service.ProductsService;
import com.ecommerce.ecommerce_site.service.UserRegisterService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class HomeController {

    @Autowired
    private UserRegisterService userRegisterService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductsService productsService;
    @Autowired
    private CartsRepo cartsRepo;

    @GetMapping("/")
    public String home(
            Authentication authentication,
            @RequestParam(value = "error", required = false) String error,
            Model model,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "home";  // show default home
        }

        Object principal = authentication.getPrincipal();
        Users user = null;
        if (principal instanceof OAuth2User oauthUser) {
            String email = oauthUser.getAttribute("email");

            if(userRegisterService.needsProfileUpdateFromGoogle(email)){
                String fname = oauthUser.getAttribute("given_name");
                String lname = oauthUser.getAttribute("family_name");
                String picture =  oauthUser.getAttribute("picture");
                user = userRegisterService.updateProfileFromGoogle(email, fname, lname, picture, "google");
            }
            else{
                user = userRegisterService.findUserByEmail(email);
            }

        }
        else{
            UserDetails userDetails = (UserDetails) principal;
            user = userRegisterService.findUserByEmail(userDetails.getUsername());
        }
        mergeCarts(httpServletRequest, httpServletResponse, user);
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/login")
    public String loginView(){
        return "home";
    }

    @GetMapping("/shop")
    public String Shop(Model model){
        addAuthenticatedUserToModel(model);
        List<CategoryDTO> categories  = categoryService.getCategories();
        model.addAttribute("brands", productsService.getAllBrandName());
        model.addAttribute("categories",categories);
        model.addAttribute("breadcrumbs", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Shop", "url", "/shop")
        ));
        return "shop";
    }

    @GetMapping("/product/{id}")
    public String productPage(@PathVariable String id, Model model){
        List<ProductDTO> productDTOS =  productsService.findProductByIds(List.of(Long.parseLong(id)));
        model.addAttribute("product", productDTOS.isEmpty() ? null : productDTOS.getFirst());
        addAuthenticatedUserToModel(model);
        model.addAttribute("breadcrumbs", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Shop", "url", "/shop"),
                Map.of("label", "Product Details", "url", "/product/")
        ));

        return "productPage";
    }
    private void addAuthenticatedUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        String email = null;

        if (principal instanceof DefaultOidcUser oidcUser) {

            email = oidcUser.getAttribute("email");

        } else if (principal instanceof UserPrincipal userPrincipal) {

            email = userPrincipal.getUsername();
        }

        if (email != null) {
            model.addAttribute("user", userRegisterService.findUserByEmail(email));
        }
    }

    @GetMapping("/shop/cart")
    private String cart(Model model){
        addAuthenticatedUserToModel(model);
        model.addAttribute("breadcrumbs", List.of(
                Map.of("label", "Shop", "url", "/shop"),
                Map.of("label", "Cart", "url", "/shop/cart")
        ));
        return "cart";
    }
    @GetMapping("/shop/favorites")
    private String favoriteProducts(Model model){
        addAuthenticatedUserToModel(model);
        return "favorite";
    }

    @GetMapping("/shop/orders")
    private String order(Model model){
        addAuthenticatedUserToModel(model);
        return "order";
    }

    @GetMapping("/shop/checkout")
    private String checkout(Model model){
        addAuthenticatedUserToModel(model);
        return "checkout";
    }

    public void mergeCarts(HttpServletRequest request, HttpServletResponse response, Users users) {
        String cartToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("cart_token".equals(cookie.getName())) {
                    cartToken = cookie.getValue();
                    break;
                }
            }
        }

        if (cartToken == null) return;

        Optional<Carts> optionalTokenCart = cartsRepo.findByCartTokenAndCartStatus(cartToken, CartStatus.ACTIVE);
        if (optionalTokenCart.isEmpty()) return;

        Carts tokenCart = optionalTokenCart.get();

        Optional<Carts> optionalUserCart = cartsRepo.findByUserAndCartStatus(users, CartStatus.ACTIVE);
        if (optionalUserCart.isPresent()) {
            Carts userCart = optionalUserCart.get();

            for(CartItems cartItems : tokenCart.getCartItems()){
                cartItems.setCart(userCart);
            }
            cartsRepo.save(userCart);
        } else {
            tokenCart.setUser(users);
            cartsRepo.save(tokenCart);
        }
    }

}
