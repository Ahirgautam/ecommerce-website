package com.ecommerce.ecommerce_site.controller.web;

import com.ecommerce.ecommerce_site.dto.CategoryDTO;
import com.ecommerce.ecommerce_site.model.Categories;
import com.ecommerce.ecommerce_site.model.Enums.OrderStatus;
import com.ecommerce.ecommerce_site.model.Enums.PaymentMethod;
import com.ecommerce.ecommerce_site.model.Enums.PaymentStatus;
import com.ecommerce.ecommerce_site.model.UserPrincipal;
import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.repo.RolesRepo;
import com.ecommerce.ecommerce_site.repo.UserRepo;
import com.ecommerce.ecommerce_site.service.CategoryService;
import com.ecommerce.ecommerce_site.service.ProductsService;
import com.ecommerce.ecommerce_site.service.UserRegisterService;
import com.ecommerce.ecommerce_site.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRegisterService userRegisterService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductsService productsService;
    @Autowired
    private RolesRepo rolesRepo;
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model){
        addAuthenticatedUserToModel(model);
        return "admin";
    }

    @GetMapping("/products")
    public String products(Model model){
        List<CategoryDTO> categories = categoryService.getCategories();
        model.addAttribute("categories", categories);
        addAuthenticatedUserToModel(model);
        return "product";
    }
    @GetMapping("/users")
    public String users(Model model){
        addAuthenticatedUserToModel(model);
        model.addAttribute("roles",rolesRepo.findAll());
        return "users";
    }
    @GetMapping("/orders")
    public String orders(Model model){
        addAuthenticatedUserToModel(model);
        List<String> orderStatus = new ArrayList<>();
        for(OrderStatus orderStatus1 : OrderStatus.values()){
            orderStatus.add(orderStatus1.name());
        }
        model.addAttribute("orderStatus", orderStatus);
        List<String> paymentStatus = new ArrayList<>();
        for(PaymentStatus paymentStatus1 : PaymentStatus.values()){
            paymentStatus.add(paymentStatus1.name());
        }
        model.addAttribute("paymentStatus", paymentStatus);

        return "adminOrders";
    }

    @GetMapping("/products/add")
    public String showProductAddForm(Model model){
        List<CategoryDTO> categories = categoryService.getCategories();

        model.addAttribute("brands", productsService.getAllBrandName());
        return  "product-add";
    }

    private void addAuthenticatedUserToModel(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
    }

}
