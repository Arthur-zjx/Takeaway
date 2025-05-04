package com.example.springboot.controller;

import com.example.springboot.payload.OrderDto;
import com.example.springboot.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderDto> listAllOrders() {
        return orderService.findAllOrders();
    }
}
