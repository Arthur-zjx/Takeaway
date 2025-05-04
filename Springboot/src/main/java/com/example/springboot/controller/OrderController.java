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
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 用户提交订单 */
    @PostMapping("/orders")
    public OrderDto createOrder(@RequestBody OrderRequest req) {
        return orderService.saveOrder(req);
    }

    /** 获取当前登录用户的所有订单 */
    @GetMapping("/orders")
    public List<OrderDto> getMyOrders(Authentication authentication) {
        // 1. 从 Authentication 拿用户名
        String username = authentication.getName();
        // 2. Service 根据用户名去查订单
        return orderService.findByUsername(username);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable Long id,
            Authentication auth) {
        // 1. 拿用户名校验，只能看自己的
        String username = auth.getName();
        OrderDto dto = orderService.findById(id);
        if (!dto.getUsername().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(dto);
    }

}
