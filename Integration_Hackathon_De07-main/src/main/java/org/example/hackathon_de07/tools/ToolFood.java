package org.example.hackathon_de07.tools;

import lombok.RequiredArgsConstructor;
import org.example.hackathon_de07.model.entity.FoodCategory;
import org.example.hackathon_de07.model.entity.FoodItem;
import org.example.hackathon_de07.service.FoodOrderService;
import org.example.hackathon_de07.service.RAGService;
import org.example.hackathon_de07.repository.FoodCategoryRepository;
import org.example.hackathon_de07.repository.FoodItemRepository;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ToolFood {

    private final FoodItemRepository foodItemRepository;
    private final FoodCategoryRepository foodCategoryRepository;
    private final FoodOrderService foodOrderService;
    private final RAGService ragService;

    @McpTool(
            name = "searchFoodByName",
            description = "Tìm món ăn theo tên hoặc từ khóa và trả về tên, giá, tồn kho, danh mục món ăn.",
            title = "Tìm món ăn theo tên",
            annotations = @McpTool.McpAnnotations(title = "Tìm món ăn theo tên", readOnlyHint = true, destructiveHint = false, openWorldHint = false),
            generateOutputSchema = true
    )
    public List<FoodItemSummary> searchFoodByName(
            @McpToolParam(description = "Tên món ăn hoặc từ khóa cần tìm.") String keyword
    ) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return foodItemRepository.findAll().stream()
                .filter(item -> matchesKeyword(item, normalized))
                .map(FoodItemSummary::from)
                .collect(Collectors.toList());
    }

    @McpTool(
            name = "searchFoodByCategory",
            description = "Trả về danh sách món ăn thuộc một danh mục món ăn.",
            title = "Tìm món ăn theo danh mục",
            annotations = @McpTool.McpAnnotations(title = "Tìm món ăn theo danh mục", readOnlyHint = true, destructiveHint = false, openWorldHint = false),
            generateOutputSchema = true
    )
    public List<FoodItemSummary> searchFoodByCategory(
            @McpToolParam(description = "Tên danh mục món ăn cần tra cứu.") String categoryName
    ) {
        String normalized = categoryName == null ? "" : categoryName.trim().toLowerCase(Locale.ROOT);
        return foodItemRepository.findAll().stream()
                .filter(item -> item.getFoodFoodCategory() != null)
                .filter(item -> item.getFoodFoodCategory().getName() != null)
                .filter(item -> item.getFoodFoodCategory().getName().toLowerCase(Locale.ROOT).contains(normalized))
                .map(FoodItemSummary::from)
                .collect(Collectors.toList());
    }

    @McpTool(
            name = "GetRestaurantInfo",
            description = "Nhận câu hỏi của khách về nhà hàng và thực hiện similarity search trên vector_story để trả về câu trả lời phù hợp.",
            title = "Hỏi thông tin nhà hàng",
            annotations = @McpTool.McpAnnotations(title = "Hỏi thông tin nhà hàng", readOnlyHint = true, destructiveHint = false, openWorldHint = false),
            generateOutputSchema = true
    )
    public String getRestaurantInfo(
            @McpToolParam(description = "Câu hỏi của khách về nhà hàng.") String question
    ) {
        return ragService.searchDocument(question);
    }

    @McpTool(
            name = "createFoodOrder",
            description = "Tạo đơn hàng món ăn mới cho một khách hàng theo danh sách món và số lượng.",
            title = "Tạo đơn hàng món ăn",
            annotations = @McpTool.McpAnnotations(title = "Tạo đơn hàng món ăn", readOnlyHint = false, destructiveHint = true, idempotentHint = false, openWorldHint = false),
            generateOutputSchema = true
    )
    public FoodOrderResult createFoodOrder(
            @McpToolParam(description = "ID của khách hàng đặt món.") Long dinerId,
            @McpToolParam(description = "Danh sách món cần đặt, gồm foodItemId và quantity.") List<CreateOrderItemRequest> items,
            @McpToolParam(required = false, description = "Ghi chú bổ sung cho đơn hàng, ví dụ nguồn đặt hàng hoặc yêu cầu đặc biệt.") String note
    ) {
        return foodOrderService.createFoodOrder(dinerId, items, note);
    }

    private boolean matchesKeyword(FoodItem item, String keyword) {
        if (keyword.isEmpty()) {
            return true;
        }
        String name = item.getName() == null ? "" : item.getName().toLowerCase(Locale.ROOT);
        String description = item.getDescription() == null ? "" : item.getDescription().toLowerCase(Locale.ROOT);
        String categoryName = item.getFoodFoodCategory() != null && item.getFoodFoodCategory().getName() != null
                ? item.getFoodFoodCategory().getName().toLowerCase(Locale.ROOT)
                : "";
        return name.contains(keyword) || description.contains(keyword) || categoryName.contains(keyword);
    }

    public record FoodItemSummary(Long id, String name, BigDecimal price, Integer stock, String category) {
        public static FoodItemSummary from(FoodItem item) {
            String categoryName = item.getFoodFoodCategory() == null ? null : item.getFoodFoodCategory().getName();
            return new FoodItemSummary(item.getId(), item.getName(), item.getPrice(), item.getStock(), categoryName);
        }
    }

    public record CreateOrderItemRequest(
            @McpToolParam(description = "ID của món ăn.") Long foodItemId,
            @McpToolParam(description = "Số lượng món ăn cần đặt.") Integer quantity
    ) {
    }

    public record FoodOrderResult(Long orderId, Long dinerId, String status, BigDecimal totalAmount, String note) {
    }

}
