package com.ecommerce.ecommerce_site.service;

import com.ecommerce.ecommerce_site.dto.OrderDTO;
import com.ecommerce.ecommerce_site.dto.OrderItemDTO;
import com.ecommerce.ecommerce_site.model.*;
import com.ecommerce.ecommerce_site.model.Enums.CartStatus;
import com.ecommerce.ecommerce_site.model.Enums.OrderStatus;
import com.ecommerce.ecommerce_site.model.Enums.PaymentMethod;
import com.ecommerce.ecommerce_site.model.Enums.PaymentStatus;
import com.ecommerce.ecommerce_site.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private CartsRepo cartsRepo;
    @Autowired
    private OrdersRepo ordersRepo;
    @Autowired
    private OrderItemsRepo orderItemsRepo;
    @Autowired
    private CustomersRepo customersRepo;
    @Autowired
    private ShippingAddressRepo shippingAddressRepo;
    @Autowired
    private ProductVariantsRepo productVariantsRepo;

    public void saveOrder(Map<String, String> map, Users users) throws Exception {
        Carts carts = cartsRepo.findByUserAndCartStatus(users, CartStatus.ACTIVE).get();
        Orders orders = new Orders();
        BigDecimal totalAmount = BigDecimal.ZERO;

        try {
            String paymentMethod = map.get("payment");
            if (paymentMethod.equals("cod")) {
                orders.setPaymentMethod(PaymentMethod.COD);
            } else if (paymentMethod.equals("upi")) {
                orders.setPaymentMethod(PaymentMethod.UPI);
            } else if (paymentMethod.equals("card")) {
                orders.setPaymentMethod(PaymentMethod.CARD);
            } else {
                orders.setPaymentMethod(PaymentMethod.WALLET);
            }

            String shippingType = map.get("shipping");
            if (shippingType.equals("express")) {
                orders.setShippingFee(new BigDecimal(120));
            } else if (shippingType.equals("overnight")) {
                orders.setShippingFee(new BigDecimal(250));
            }

            Customers customers = new Customers(map.get("fullName"), map.get("email"), map.get("mobile"));
            customersRepo.save(customers);

            ShippingAddress shippingAddress = new ShippingAddress(
                    map.get("address1"), map.get("address2"), map.get("state"), map.get("city"), map.get("pin"));
            shippingAddressRepo.save(shippingAddress);


            orders.setShippingAddresses(shippingAddress);
            orders.setCustomers(customers);
            orders.setUser(users);
            orders.setCarts(carts);
            orders = ordersRepo.save(orders);


            for (CartItems cartItems : carts.getCartItems()) {
                OrderItems orderItems = new OrderItems();
                orderItems.setPrice(cartItems.getPriceAtAddition());
                orderItems.setProducts(cartItems.getProducts());
                orderItems.setProductVariants(cartItems.getProductVariants());
                orderItems.setQuantity(cartItems.getQuantity());
                orderItems.setOrders(orders);

                BigDecimal total = cartItems.getPriceAtAddition()
                        .multiply(BigDecimal.valueOf(cartItems.getQuantity()));
                orderItems.setTaxAmount(total.multiply(BigDecimal.valueOf(0.10)));
                orderItems.setSubTotal(total.add(orderItems.getTaxAmount()));

                totalAmount = totalAmount.add(orderItems.getSubTotal());
                orderItemsRepo.save(orderItems);
                ProductVariants productVariants = cartItems.getProductVariants();
                productVariants.setStock(productVariants.getStock() - cartItems.getQuantity());
                productVariantsRepo.save(productVariants);
            }

            carts.setCartStatus(CartStatus.CONVERTED);
            orders.setTotalAmount(totalAmount);
            ordersRepo.save(orders);
            cartsRepo.save(carts);

        } catch (Exception e) {
            System.out.println("Error :" + e.toString());
            throw e;
        }
    }

    public List<OrderDTO> getOrdersByUser(Users users) throws Exception{
        List<Orders> orders = ordersRepo.findByUser(users);
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for(Orders order : orders){
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderId(order.getOrderId());
            orderDTO.setOrderStatus(order.getOrderStatus());
            orderDTO.setPaymentMethod(order.getPaymentMethod());
            orderDTO.setPaymentStatus(order.getPaymentStatus());
            orderDTO.setTotal(order.getTotalAmount());
            orderDTO.setCreatedAt(order.getCreatedAt());
            orderDTO.setAddress(order.getShippingAddresses().getAddressLineOne());
            orderDTO.setState(order.getShippingAddresses().getState());
            orderDTO.setCity(order.getShippingAddresses().getCity());
            orderDTO.setPinCode(order.getShippingAddresses().getPinCode());
            orderDTO.setFullName(order.getCustomers().getFullName());
            orderDTO.setEmail(order.getCustomers().getEmail());
            orderDTO.setNumber(order.getCustomers().getMobileNumber());
            List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
            for(OrderItems orderItem : order.getOrderItems()){
                OrderItemDTO orderItemDTO = new OrderItemDTO();
                orderItemDTO.setBrand(orderItem.getProducts().getBrandName());
                orderItemDTO.setCategory(orderItem.getProducts().getCategory().getName());
                orderItemDTO.setQuantity(orderItem.getQuantity());
                orderItemDTO.setProductName(orderItem.getProducts().getName());
                orderItemDTO.setPrice(orderItem.getProductVariants().getDiscountedPrice());
                orderItemDTO.setImage(orderItem.getProducts().getVariants().stream().findFirst().get().getImages().stream().findFirst().get().getImg_url());
                orderItemDTOList.add(orderItemDTO);
            }
            orderDTO.setOrderItemDTOS(orderItemDTOList);
            orderDTOS.add(orderDTO);
        }
        return orderDTOS;
    }

    public void deleteOrder(Long id) throws Exception{
        Orders orders = ordersRepo.findById(id).orElseThrow();
        ordersRepo.delete(orders);
        //write the logic of incrementing product stock
    }

    public void updateOrderStatusAndPaymentStatus(Long id,Map<String, String> map) throws Exception{
        Orders orders = ordersRepo.findById(id).orElseThrow();
        orders.setOrderStatus(OrderStatus.valueOf(map.get("orderStatus")));
        orders.setPaymentStatus(PaymentStatus.valueOf(map.get("paymentStatus")));
        ordersRepo.save(orders);
    }
}
