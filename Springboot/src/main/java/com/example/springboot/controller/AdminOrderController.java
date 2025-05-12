package com.example.springboot.controller;

import com.example.springboot.payload.OrderDto;
import com.example.springboot.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PatchMapping("/{id}/status")
    public OrderDto changeOrderStatus(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body) {

        String newStatus = body.get("status");
        return orderService.updateStatus(id, newStatus);
    }

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.findById(id);
    }

}
