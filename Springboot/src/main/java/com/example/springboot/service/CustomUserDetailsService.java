package com.example.springboot.service;

import com.example.springboot.model.User;
import com.example.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库读出 User 实体（含 username, password, role）
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 根据 user.getRole() 构造一个 ROLE_xxx 权限
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getRole());

        // 返回 Spring Security 内置的 UserDetails 实现
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),       // 你的密码是 BCrypt 存好的，配合 BCryptPasswordEncoder 就能校验
                Collections.singleton(authority)
        );
    }
}
