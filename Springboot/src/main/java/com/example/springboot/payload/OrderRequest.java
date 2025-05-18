package com.example.springboot.payload;

import java.util.List;

public class OrderRequest {
    private Long userId;            // 可选：如果前端提交 userId
    private String username;        // 或者直接用 username
    private String phone;
    private String address;
    private List<Item> dishes;// 对应前端的 dishes 数组

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    private String recipientName;// 前端填写的收货人姓名

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
