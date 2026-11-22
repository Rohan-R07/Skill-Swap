package com.example.backend.controller;

import com.example.backend.dto.ConversationResponse;
import com.example.backend.dto.MessagePageResponse;
import com.example.backend.dto.MessageResponse;
import com.example.backend.dto.SendMessageRequest;
import com.example.backend.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) { this.chatService = chatService; }

    @GetMapping("/{userId}")
    public List<ConversationResponse> getConversations(@PathVariable Long userId) {
        return chatService.getConversations(userId);
    }

    @GetMapping("/{conversationId}/messages")
    public MessagePageResponse getMessages(@PathVariable Long conversationId,
                                           @RequestParam Long userId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "50") int size) {
        return chatService.getMessages(conversationId, userId, page, size);
    }

    @PostMapping("/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse sendMessage(@PathVariable Long conversationId,
                                       @Valid @RequestBody SendMessageRequest request) {
        return chatService.sendMessage(conversationId, request);
    }
}
