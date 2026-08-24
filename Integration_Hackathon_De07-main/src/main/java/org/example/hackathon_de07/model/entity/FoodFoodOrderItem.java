package org.example.hackathon_de07.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "food_food_foodFoodOrder_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodFoodOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foodFoodOrder_id", nullable = false)
    private FoodOrder foodFoodOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foodItem_id", nullable = false)
    private FoodItem foodItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
}
