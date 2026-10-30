package com.abdoheddaya.assistantservice.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder, ToolCallbackProvider tools) {
        this.chatClient = builder
                .defaultSystem("You are a banking assistant. Use MCP tools to verify account and transaction facts. Never invent balances or transaction results. Execute money-moving operations only when explicitly requested by the user.")
                .defaultToolCallbacks(tools)
                .build();
    }

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        String answer = chatClient.prompt().user(request.message()).call().content();
        return new ChatResponse(answer);
    }

    public record ChatRequest(@NotBlank String message) {}
    public record ChatResponse(String answer) {}
}
