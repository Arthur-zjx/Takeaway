package com.example.springboot.repository;

import com.example.springboot.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    // 1) Generate queries automatically via Spring Data JPA naming conventions
    int countByStatus(String status);
    List<Dish> findAllByStatus(String status);
    // Or define custom JPQL/SQL queries
    // @Query("SELECT COUNT(d) FROM Dish d WHERE d.status = :status")
    // int countByStatus(@Param("status") String status);
}
