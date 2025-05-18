package com.example.springboot.payload;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    private Long id;
    private String username;
    private String phone;
    private String address;
    private String status;
    private Double total;
    private LocalDateTime createdAt;
    private List<OrderItemDto> dishes;

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    private String recipientName;

    // --- getters & setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<OrderItemDto> getDishes() { return dishes; }
    public void setDishes(List<OrderItemDto> dishes) { this.dishes = dishes; }
}
