package com.example.onlinefoodordering.controller;

import com.example.onlinefoodordering.model.Order;
import com.example.onlinefoodordering.model.User;
import com.example.onlinefoodordering.request.OrderRequest;
import com.example.onlinefoodordering.service.OrderService;
import com.example.onlinefoodordering.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/order")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request,
                                             @RequestHeader("Authorization") String jwt)throws Exception{
        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.createOrder(request,user);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/order/user")
    public ResponseEntity<List<Order>> getOrderHistory(@RequestHeader("Authorization") String jwt)throws Exception{
        User user = userService.findUserByJwtToken(jwt);
        List<Order> orderList = orderService.getUsersOder(user.getId());
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }
}
