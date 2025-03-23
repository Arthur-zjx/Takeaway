package com.example.springboot.model;

public class UserTest {
    public static void main(String[] args) {
        // 创建 User 实例
        User user = new User();

        // 设置 username
        user.setUsername("TestUser");

        // 获取 username 并打印
        System.out.println("Username: " + user.getUsername());

        // 设置 email
        user.setEmail("test@example.com");

        // 获取 email 并打印
        System.out.println("Email: " + user.getEmail());
    }
}
