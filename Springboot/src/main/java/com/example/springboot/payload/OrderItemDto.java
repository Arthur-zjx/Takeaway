package com.example.springboot.payload;

public class OrderItemDto {
    private String name;
    private Integer quantity;
    private Double price;

    // --- getters & setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
