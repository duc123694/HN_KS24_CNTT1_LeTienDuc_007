package org.example.hackathon_de07.controller;

import org.example.hackathon_de07.service.FoodOrderService;
import org.example.hackathon_de07.service.RAGService;
import org.example.hackathon_de07.tools.ToolFood;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
public class ToolFoodController {

    private final ToolFood toolFood;
    private final FoodOrderService foodOrderService;
    private final RAGService ragService;

    public ToolFoodController(ToolFood toolFood, FoodOrderService foodOrderService, RAGService ragService) {
        this.toolFood = toolFood;
        this.foodOrderService = foodOrderService;
        this.ragService = ragService;
    }

    @GetMapping("/search-food-by-name")
    public ResponseEntity<List<ToolFood.FoodItemSummary>> searchFoodByName(@RequestParam String keyword) {
        return ResponseEntity.ok(toolFood.searchFoodByName(keyword));
    }

    @GetMapping("/search-food-by-category")
    public ResponseEntity<List<ToolFood.FoodItemSummary>> searchFoodByCategory(@RequestParam String categoryName) {
        return ResponseEntity.ok(toolFood.searchFoodByCategory(categoryName));
    }

    @GetMapping("/restaurant-info")
    public ResponseEntity<String> getRestaurantInfo(@RequestParam String question) {
        String answer = ragService.searchDocument(question);
        if (answer == null || answer.isBlank()) {
            answer = "Không tìm thấy thông tin phù hợp trong vector_story.";
        }
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/create-food-order")
    public ResponseEntity<ToolFood.FoodOrderResult> createFoodOrder(@RequestBody CreateFoodOrderRequest request) {
        return ResponseEntity.ok(foodOrderService.createFoodOrder(request.dinerId(), request.items(), request.note()));
    }

    public record CreateFoodOrderRequest(
            Long dinerId,
            List<ToolFood.CreateOrderItemRequest> items,
            String note
    ) {
    }
}
