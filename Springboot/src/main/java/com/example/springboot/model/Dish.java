package com.example.springboot.model;

import jakarta.persistence.*;
import lombok.Data;

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
}
