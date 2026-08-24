package org.example.hackathon_de07.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hackathon_de07.model.entity.*;
import org.example.hackathon_de07.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializeService {

    private final FoodCategoryRepository foodCategoryRepository;
    private final FoodItemRepository foodItemRepository;
    private final DinerRepository dinerRepository;

    @PostConstruct
    public void initializeDatabase() {
        if (foodCategoryRepository.count() == 0) {
            System.out.println("Initializing generic data for De07...");
            FoodCategory c1 = foodCategoryRepository.save(new FoodCategory(null, "Type A", "Description A"));
            FoodCategory c2 = foodCategoryRepository.save(new FoodCategory(null, "Type B", "Description B"));
            
            foodItemRepository.saveAll(List.of(
                new FoodItem(null, "Item 1", "Desc 1", new BigDecimal("100000"), 50, null, c1),
                new FoodItem(null, "Item 2", "Desc 2", new BigDecimal("200000"), 30, null, c2)
            ));
            
            dinerRepository.saveAll(List.of(
                new Diner(null, "User A", "0901234567", "a@example.com", "Address A"),
                new Diner(null, "User B", "0912345678", "b@example.com", "Address B")
            ));
        }
    }
}
