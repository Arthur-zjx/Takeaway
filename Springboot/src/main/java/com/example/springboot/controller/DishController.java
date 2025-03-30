package com.example.springboot.controller;

import com.example.springboot.model.Dish;
import com.example.springboot.repository.DishRepository;
import com.example.springboot.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/dish")
public class DishController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private DishRepository dishRepository;

    // 1. 获取单个菜品信息
    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable("id") Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));
        return ResponseEntity.ok(dish);
    }

    // 2. 获取所有菜品信息
    @GetMapping("/")
    public ResponseEntity<List<Dish>> getAllDishes() {
        List<Dish> dishes = dishRepository.findAll();
        return ResponseEntity.ok(dishes);
    }

    // 3. 创建菜品并上传图片
    @PostMapping("/upload")
    public ResponseEntity<Dish> uploadDish(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("status") String status
    ) {
        String imageUrl = s3Service.uploadFile(file);

        Dish dish = new Dish();
        dish.setName(name);
        dish.setCategory(category);
        dish.setPrice(price);
        dish.setDescription(description);
        dish.setStatus(status);
        dish.setImageUrl(imageUrl);

        Dish savedDish = dishRepository.save(dish);
        return ResponseEntity.ok(savedDish);
    }

    // 4. 更新菜品信息
    @PutMapping("/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable("id") Long id,
                                           @RequestBody Dish updatedDish) {
        // 根据 id 查找已有菜品
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));

        // 更新菜品的各项信息
        dish.setName(updatedDish.getName());
        dish.setPrice(updatedDish.getPrice());
        dish.setCategory(updatedDish.getCategory());
        dish.setStatus(updatedDish.getStatus());
        dish.setDescription(updatedDish.getDescription());
        dish.setImageUrl(updatedDish.getImageUrl());

        // 保存更新后的菜品
        Dish savedDish = dishRepository.save(dish);
        return ResponseEntity.ok(savedDish);
    }

    // 5. 删除菜品
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDish(@PathVariable("id") Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));
        dishRepository.delete(dish);
        return ResponseEntity.ok().build();
    }

    // 新增接口：只上传图片，返回图片 URL
    @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = s3Service.uploadFile(file);
        return ResponseEntity.ok(imageUrl);
    }


}
