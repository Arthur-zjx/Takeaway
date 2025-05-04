package com.example.springboot.repository;

import com.example.springboot.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * 如果你未来要实现“用户查看自己订单”功能，就可以用这个方法：
     * List<Order> findByUsername(String username);
     * 或者，如果你是按 userId 存储的：
     * List<Order> findByUserId(Long userId);
     */
    List<Order> findByUsername(String username);
}
