package com.example.springboot.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String description;
    private String imageUrl;
    private Double price;
    private String status;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
