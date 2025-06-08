package com.example.springboot.model;

public class UserTest {
    public static void main(String[] args) {
        // Create a User instance
        User user = new User();

        // Set username
        user.setUsername("TestUser");

        // Get username and print
        System.out.println("Username: " + user.getUsername());

        // Set email
        user.setEmail("test@example.com");

        // Get email and print
        System.out.println("Email: " + user.getEmail());
    }
}
