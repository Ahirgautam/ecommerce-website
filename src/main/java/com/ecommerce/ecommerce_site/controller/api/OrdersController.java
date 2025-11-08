package com.ecommerce.ecommerce_site.controller.api;

import com.ecommerce.ecommerce_site.dto.OrderDTO;
import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.service.OrderService;
import com.ecommerce.ecommerce_site.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OrdersController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    @PostMapping("/api/orders")
    public ResponseEntity<?> saveOrder(@RequestBody Map<String, String>map){
        Users users = userService.getCurrentUser();
        try {
            orderService.saveOrder(map, users);
            return ResponseEntity.ok("Order Placed");
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/api/orders")
    public ResponseEntity<?> getAllOrders(){
        Users users = userService.getCurrentUser();
        try{
            List<OrderDTO> orderDTOS =  orderService.getOrdersByUser(users);
            return ResponseEntity.ok(orderDTOS);
        }
        catch (Exception e){
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/api/orders/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id){
        try{
            orderService.deleteOrder(id);
            return new ResponseEntity<>("order deleted", HttpStatus.NO_CONTENT);
        }
        catch (Exception e){
            System.out.println("Error" + e);
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/api/orders/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Map<String, String> map){
        try{
            orderService.updateOrderStatusAndPaymentStatus(id, map);
            return ResponseEntity.ok("updated");
        }catch (Exception e){
            System.out.println("Error " + e);
            return ResponseEntity.badRequest().build();
        }
    }
}
