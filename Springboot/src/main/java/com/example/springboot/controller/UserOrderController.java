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

    /** 用户提交订单 */
    @PostMapping("/orders")
    public OrderDto createOrder(
            @RequestBody OrderRequest req,
            Authentication authentication   // 注入当前登录用户
    ) {
        // 从 Authentication 中拿到真实登录用户名
        String loginUsername = authentication.getName();
        // 交给 Service：登录名 + 前端填写的 recipientName + 其它字段
        return orderService.saveOrder(loginUsername, req);
    }

    /** 获取当前登录用户的所有订单 */
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
