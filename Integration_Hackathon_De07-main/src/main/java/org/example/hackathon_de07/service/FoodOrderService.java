package org.example.hackathon_de07.service;

import org.example.hackathon_de07.tools.ToolFood;
import org.example.hackathon_de07.model.constant.FoodOrderStatus;
import org.example.hackathon_de07.model.entity.Diner;
import org.example.hackathon_de07.model.entity.FoodFoodOrderItem;
import org.example.hackathon_de07.model.entity.FoodItem;
import org.example.hackathon_de07.model.entity.FoodOrder;
import org.example.hackathon_de07.repository.DinerRepository;
import org.example.hackathon_de07.repository.FoodFoodOrderItemRepository;
import org.example.hackathon_de07.repository.FoodItemRepository;
import org.example.hackathon_de07.repository.FoodOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FoodOrderService {

    private final DinerRepository dinerRepository;
    private final FoodItemRepository foodItemRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final FoodFoodOrderItemRepository foodFoodOrderItemRepository;

    public FoodOrderService(
            DinerRepository dinerRepository,
            FoodItemRepository foodItemRepository,
            FoodOrderRepository foodOrderRepository,
            FoodFoodOrderItemRepository foodFoodOrderItemRepository
    ) {
        this.dinerRepository = dinerRepository;
        this.foodItemRepository = foodItemRepository;
        this.foodOrderRepository = foodOrderRepository;
        this.foodFoodOrderItemRepository = foodFoodOrderItemRepository;
    }

    public ToolFood.FoodOrderResult createFoodOrder(Long dinerId, List<ToolFood.CreateOrderItemRequest> items, String note) {
        if (dinerId == null) {
            throw new IllegalArgumentException("dinerId is required");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items is required");
        }

        Diner diner = dinerRepository.findById(dinerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng"));

        BigDecimal totalAmount = BigDecimal.ZERO;
        FoodOrder order = new FoodOrder();
        order.setDiner(diner);
        order.setFoodFoodOrderDate(LocalDateTime.now());
        order.setStatus(FoodOrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setNote(note);
        order = foodOrderRepository.save(order);

        for (ToolFood.CreateOrderItemRequest request : items) {
            if (request == null || request.foodItemId() == null || request.quantity() == null || request.quantity() <= 0) {
                throw new IllegalArgumentException("Mỗi item phải có foodItemId và quantity > 0");
            }

            FoodItem foodItem = foodItemRepository.findById(request.foodItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món ăn: " + request.foodItemId()));
            if (foodItem.getStock() < request.quantity()) {
                throw new IllegalArgumentException("Món " + foodItem.getName() + " không đủ tồn kho");
            }

            foodItem.setStock(foodItem.getStock() - request.quantity());
            foodItemRepository.save(foodItem);

            FoodFoodOrderItem orderItem = new FoodFoodOrderItem();
            orderItem.setFoodFoodOrder(order);
            orderItem.setFoodItem(foodItem);
            orderItem.setQuantity(request.quantity());
            orderItem.setUnitPrice(foodItem.getPrice());
            foodFoodOrderItemRepository.save(orderItem);

            totalAmount = totalAmount.add(foodItem.getPrice().multiply(BigDecimal.valueOf(request.quantity())));
        }

        order.setTotalAmount(totalAmount);
        order = foodOrderRepository.save(order);

        return new ToolFood.FoodOrderResult(
                order.getId(),
                diner.getId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getNote()
        );
    }

}
