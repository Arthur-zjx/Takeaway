package com.example.springboot.controller;

import com.example.springboot.model.Dish;
import com.example.springboot.repository.DishRepository;
import com.example.springboot.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/dish")
public class DishController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private DishRepository dishRepository;

    // 1. Get a single dish by ID
    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable("id") Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));
        return ResponseEntity.ok(dish);
    }

    // 2. Get all dishes (user side shows available only unless showAll=true)
    @GetMapping("/")
    public ResponseEntity<List<Dish>> getAllDishes(
            @RequestParam(name = "showAll", defaultValue = "false") boolean showAll) {
        List<Dish> list = showAll
                ? dishRepository.findAll()                       // Admin side: show all
                : dishRepository.findAllByStatus("available");   // User side: show only "available"
        return ResponseEntity.ok(list);
    }

    // 3. Create a dish and upload image
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

    // 4. Update dish information
    @PutMapping("/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable("id") Long id,
                                           @RequestBody Dish updatedDish) {
        // Find existing dish by ID
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));

        // Update dish properties
        dish.setName(updatedDish.getName());
        dish.setPrice(updatedDish.getPrice());
        dish.setCategory(updatedDish.getCategory());
        dish.setStatus(updatedDish.getStatus());
        dish.setDescription(updatedDish.getDescription());
        dish.setImageUrl(updatedDish.getImageUrl());

        // Save the updated dish
        Dish savedDish = dishRepository.save(dish);
        return ResponseEntity.ok(savedDish);
    }

    // 5. Delete a dish
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDish(@PathVariable("id") Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));
        dishRepository.delete(dish);
        return ResponseEntity.ok().build();
    }

    // Extra endpoint: upload image only and return image URL
    @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = s3Service.uploadFile(file);
        return ResponseEntity.ok(imageUrl);
    }

    // Extra endpoint: get dish statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getDishStats() {
        int activeCount = dishRepository.countByStatus("available");
        int inactiveCount = dishRepository.countByStatus("unavailable");
        Map<String, Integer> stats = new HashMap<>();
        stats.put("active", activeCount);
        stats.put("inactive", inactiveCount);
        return ResponseEntity.ok(stats);
    }


}
