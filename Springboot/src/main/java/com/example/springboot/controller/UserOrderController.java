package com.example.springboot.controller;

import com.example.springboot.payload.OrderDto;
import com.example.springboot.payload.OrderRequest;
import com.example.springboot.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserOrderController {

    private final OrderService orderService;

    public UserOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** User places an order */
    @PostMapping("/orders")
    public OrderDto createOrder(
            @RequestBody OrderRequest req,
            Authentication authentication   // Inject the current authenticated user
    ) {
        // Retrieve the authenticated username
        String loginUsername = authentication.getName();
        // Pass to service: login username + recipientName from frontend + other fields
        return orderService.saveOrder(loginUsername, req);
    }

    /** Get all orders for the current user */
    @GetMapping("/orders")
    public List<OrderDto> getMyOrders(Authentication authentication) {
        String username = authentication.getName();
        return orderService.findByUsername(username);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        OrderDto dto = orderService.findById(id);
        if (!dto.getUsername().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(dto);
    }

}
