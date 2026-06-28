package com.minimaxi.backend.controller;

import com.minimaxi.backend.config.JwtUtil;
import com.minimaxi.backend.dto.request.AskRequest;
import com.minimaxi.backend.dto.response.AskResponse;
import com.minimaxi.backend.dto.response.ChatMessageResponse;
import com.minimaxi.backend.dto.response.ConversationResponse;
import com.minimaxi.backend.service.AiService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final AiService aiService;
    private final JwtUtil jwtUtil;

    public AiController(AiService aiService, JwtUtil jwtUtil) {
        this.aiService = aiService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/ask")
    public AskResponse ask(@RequestBody AskRequest request, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        Long orgId = jwtUtil.extractOrganizationId(token);
        return aiService.ask(request, userId, orgId);
    }

    @GetMapping("/conversations")
    public List<ConversationResponse> getConversations(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.extractUserId(token);
        return aiService.getConversations(userId);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<ChatMessageResponse> getMessages(@PathVariable Long conversationId) {
        return aiService.getMessages(conversationId);
    }
}