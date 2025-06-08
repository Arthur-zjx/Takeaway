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
    private final SimpMessagingTemplate broker;  // Inject messaging template

    // Constructor injection of OrderRepository and SimpMessagingTemplate
    public OrderService(OrderRepository orderRepository,
                        SimpMessagingTemplate broker) {
        this.orderRepository = orderRepository;
        this.broker = broker;
    }

    /**
     * User places an order: save it, return DTO to the client,
     * and broadcast the new order to /topic/orders.
     */
    @Transactional
    public OrderDto saveOrder(String loginUsername, OrderRequest req) {
        Order order = new Order();
        order.setUsername(loginUsername);
        order.setRecipientName(req.getRecipientName());
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

        // Broadcast new order
        broker.convertAndSend("/topic/orders", dto);

        return dto;
    }

    /**
     * Admin: retrieve all orders.
     */
    @Transactional(readOnly = true)
    public List<OrderDto> findAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Admin: update order status and broadcast the change to /topic/orders.
     */
    @Transactional
    public OrderDto updateStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);
        OrderDto dto = toDto(saved);

        // Broadcast status update
        broker.convertAndSend("/topic/orders", dto);

        return dto;
    }

    /**
     * User: find orders by username.
     */
    @Transactional(readOnly = true)
    public List<OrderDto> findByUsername(String username) {
        return orderRepository.findByUsername(username).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * User: find a single order detail by ID.
     */
    @Transactional(readOnly = true)
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
        return toDto(order);
    }

    // --- Private mapping method ---
    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUsername(order.getUsername());
        dto.setPhone(order.getPhone());
        dto.setAddress(order.getAddress());
        dto.setRecipientName(order.getRecipientName());
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
