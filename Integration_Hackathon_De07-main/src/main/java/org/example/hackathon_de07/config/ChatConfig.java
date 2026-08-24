package org.example.hackathon_de07.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ChatConfig {

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem("""
                        Bạn là trợ lý tiếng Việt của Food. Hãy trả lời ngắn gọn, thân thiện và chỉ dùng
                        thông tin trong kết quả công cụ. Khi khách hàng gọi tra cứu món ăn hoặc thông tin nhà hàng
                        thì sẽ gọi tool tương ứng và gọi đồ ăn cũng vậy . Không báo đặt đồ ăn thành công trước khi
                        công cụ xác nhận. Không tự bịa dữ liệu.
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory()).build()
                )
                .defaultTools()
                .build();
    }
}