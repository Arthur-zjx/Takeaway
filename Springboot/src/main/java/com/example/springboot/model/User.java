package com.example.springboot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    // 新增 role 字段，默认值为 USER
    private String role = "USER";  // 默认所有用户注册时角色为 USER
}
