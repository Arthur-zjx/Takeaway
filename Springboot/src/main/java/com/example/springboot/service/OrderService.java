package com.example.springboot.service;

import com.example.springboot.model.Order;
import com.example.springboot.model.OrderItem;
import com.example.springboot.payload.OrderDto;
import com.example.springboot.payload.OrderItemDto;
import com.example.springboot.payload.OrderRequest;
import com.example.springboot.repository.OrderRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate broker;  // 注入消息模板

    // 构造器注入 OrderRepository 和 SimpMessagingTemplate
    public OrderService(OrderRepository orderRepository,
                        SimpMessagingTemplate broker) {
        this.orderRepository = orderRepository;
        this.broker = broker;
    }

    /**
     * 用户下单：保存订单并返回 DTO 给前端，同时推送到 /topic/orders
     */
    @Transactional
    public OrderDto saveOrder(OrderRequest req) {
        Order order = new Order();
        order.setUsername(req.getUsername());
        order.setPhone(req.getPhone());
        order.setAddress(req.getAddress());
        order.setStatus("pending");

        List<OrderItem> items = req.getDishes().stream()
                .map(d -> {
                    OrderItem oi = new OrderItem();
                    oi.setName(d.getName());
                    oi.setQuantity(d.getQuantity());
                    oi.setPrice(d.getPrice());
                    oi.setOrder(order);
                    return oi;
                })
                .collect(Collectors.toList());
        order.setDishes(items);

        Order saved = orderRepository.save(order);
        OrderDto dto = toDto(saved);

        // 推送新订单
        broker.convertAndSend("/topic/orders", dto);

        return dto;
    }

    /**
     * 管理端：查询所有订单
     */
    @Transactional(readOnly = true)
    public List<OrderDto> findAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 管理端：更新订单状态，并推送变更到 /topic/orders
     */
    @Transactional
    public OrderDto updateStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);
        OrderDto dto = toDto(saved);

        // 推送状态更新
        broker.convertAndSend("/topic/orders", dto);

        return dto;
    }

    /**
     * 用户端：查询某个用户的订单
     */
    @Transactional(readOnly = true)
    public List<OrderDto> findByUsername(String username) {
        return orderRepository.findByUsername(username).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 用户端：根据 ID 查询单个订单详情
     */
    @Transactional(readOnly = true)
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
        return toDto(order);
    }

    // --- 私有映射方法 ---
    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUsername(order.getUsername());
        dto.setPhone(order.getPhone());
        dto.setAddress(order.getAddress());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        double total = order.getDishes().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        dto.setTotal(total);

        List<OrderItemDto> dishDtos = order.getDishes().stream()
                .map(i -> {
                    OrderItemDto d = new OrderItemDto();
                    d.setName(i.getName());
                    d.setQuantity(i.getQuantity());
                    d.setPrice(i.getPrice());
                    return d;
                })
                .collect(Collectors.toList());
        dto.setDishes(dishDtos);

        return dto;
    }
}
