package org.example.hackathon_de07.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hackathon_de07.model.constant.FoodOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "foodFoodFoodOrders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diner_id", nullable = false)
    private Diner diner;

    @Column(nullable = false)
    private LocalDateTime foodFoodOrderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FoodOrderStatus status;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    // Ghi chú nguồn gốc đơn hàng, ví dụ: "Đặt qua AI Chatbot"
    @Column(length = 255)
    private String note;
}
