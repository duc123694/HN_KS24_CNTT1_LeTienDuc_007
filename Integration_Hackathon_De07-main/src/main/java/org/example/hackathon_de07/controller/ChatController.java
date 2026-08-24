package org.example.hackathon_de07.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    @Autowired
    private ChatClient chatClient ;
    @Autowired
    private VectorStore vectorStore ;

    @PostMapping
    public String handleChat(@RequestBody ChatRequest chatRequest){
        return chatClient.prompt()
                .user(chatRequest.message())
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", chatRequest.conversationId()))
                .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .call()
                .content();
    }
}
