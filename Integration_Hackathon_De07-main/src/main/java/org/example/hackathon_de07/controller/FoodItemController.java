package org.example.hackathon_de07.controller;

import org.example.hackathon_de07.model.entity.FoodItem;
import org.example.hackathon_de07.repository.FoodItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/foodItems")
public class FoodItemController {

    private final FoodItemRepository foodItemRepository;

    public FoodItemController(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    @GetMapping
    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItem> getFoodItemById(@PathVariable Long id) {
        FoodItem foodItem = foodItemRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Không tìm thấy sản phẩm"));
        return ResponseEntity.ok(foodItem);
    }
}
