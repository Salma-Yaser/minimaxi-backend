package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.request.AskRequest;
import com.minimaxi.backend.dto.response.AskResponse;
import com.minimaxi.backend.dto.response.ChatMessageResponse;
import com.minimaxi.backend.dto.response.ConversationResponse;

import java.util.List;

public interface AiService {
    AskResponse ask(AskRequest request, Long userId, Long organizationId);
    List<ConversationResponse> getConversations(Long userId);
    List<ChatMessageResponse> getMessages(Long conversationId);
}