package com.example.springboot.repository;

import com.example.springboot.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    // 1) 使用 Spring Data JPA 的命名规则自动生成查询
    int countByStatus(String status);
    List<Dish> findAllByStatus(String status);
    // 或者也可以写自定义 JPQL/SQL
    // @Query("SELECT COUNT(d) FROM Dish d WHERE d.status = :status")
    // int countByStatus(@Param("status") String status);
}
