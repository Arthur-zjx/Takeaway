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

    /** Customer column (username) */
    private String username;

    /** Phone column */
    private String phone;

    /** Address column */
    private String address;

    /** Status column */
    private String status = "pending";

    // Recipient name provided by frontend
    private String recipientName;

    /** Creation time (for ordering or display) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** Matches the `dishes` array from the frontend */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> dishes;

    /**
     * Not stored in DB, used only to display total to the frontend
     */
    @Transient
    public Double getTotal() {
        if (dishes == null) return 0.0;
        return dishes.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
