package com.example.springboot.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 前端“Customer”列 */
    private String username;

    /** 前端“Phone”列 */
    private String phone;

    /** 前端“Address”列 */
    private String address;

    /** 前端“Status”列 */
    private String status = "pending";

    /** 创建时间，用于排序或展示 */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** 对应前端的 `dishes` 数组 */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> dishes;

    /**
     * 不存库，仅用来给前端展示“Total”
     */
    @Transient
    public Double getTotal() {
        if (dishes == null) return 0.0;
        return dishes.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
