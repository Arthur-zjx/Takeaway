package com.example.springboot.payload;

import java.util.List;

public class OrderRequest {
    private Long userId;            // Optional: provided if the frontend sends userId
    private String username;        // Or simply use username
    private String phone;
    private String address;
    private List<Item> dishes;// Matches the dishes array from the frontend

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    private String recipientName;// Recipient name entered by the frontend

    public static class Item {
        private String name;
        private Integer quantity;
        private Double price;

        // getters & setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }

    // --- getters & setters ---
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public List<Item> getDishes() { return dishes; }
    public void setDishes(List<Item> dishes) { this.dishes = dishes; }
}
