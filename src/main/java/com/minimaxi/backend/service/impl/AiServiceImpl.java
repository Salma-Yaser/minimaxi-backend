package com.minimaxi.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimaxi.backend.dto.request.AskRequest;
import com.minimaxi.backend.dto.response.AskResponse;
import com.minimaxi.backend.dto.response.ChatMessageResponse;
import com.minimaxi.backend.dto.response.ConversationResponse;
import com.minimaxi.backend.entity.*;
import com.minimaxi.backend.enums.ChatSender;
import com.minimaxi.backend.enums.ChatStatus;
import com.minimaxi.backend.repository.*;
import com.minimaxi.backend.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;

@Service
public class AiServiceImpl implements AiService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final MachineRepository machineRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final AppUserRepository appUserRepository;
    private final OrganizationRepository organizationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiServiceImpl(MachineRepository machineRepository,
                         WorkOrderRepository workOrderRepository,
                         ChatConversationRepository conversationRepository,
                         ChatMessageRepository messageRepository,
                         AppUserRepository appUserRepository,
                         OrganizationRepository organizationRepository) {
        this.machineRepository = machineRepository;
        this.workOrderRepository = workOrderRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.appUserRepository = appUserRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    @Transactional
    public AskResponse ask(AskRequest request, Long userId, Long organizationId) {
        try {
            // 1. جيبي أو ابدأي conversation
            ChatConversation conversation = getOrCreateConversation(request, userId, organizationId);

            // 2. احفظي رسالة اليوزر
            saveMessage(conversation, ChatSender.USER, request.getMessage());

            // 3. جيبي كل الـ history من DB
            List<ChatMessage> history = messageRepository
                    .findByConversationIdOrderByCreatedAtAsc(conversation.getId());

            // 4. ابني الـ context من الماكينة/work order
            String context = buildContext(request);

            // 5. ابعتي للـ AI مع الـ history كله
            String reply = callGroq(history, context);

            // 6. احفظي رد الـ AI
            saveMessage(conversation, ChatSender.BOT, reply);

            // 7. حدّثي last_message_at
            conversation.setLastMessageAt(Instant.now());
            conversationRepository.save(conversation);

            return new AskResponse(reply, conversation.getId());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI service temporarily unavailable: " + e.getMessage());
        }
    }

    private ChatConversation getOrCreateConversation(AskRequest request, Long userId, Long organizationId) {
        // لو في conversationId → جيبيه من DB
        if (request.getConversationId() != null) {
            return conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
        }

        // لو مفيش → ابدأي واحد جديد
        ChatConversation conv = new ChatConversation();
        conv.setUser(appUserRepository.getReferenceById(userId));
        conv.setOrganization(organizationRepository.getReferenceById(organizationId));
        conv.setStatus(ChatStatus.OPEN);
        conv.setCreatedAt(Instant.now());
        conv.setLastMessageAt(Instant.now());

        if (request.getMachineId() != null) {
            machineRepository.findById(request.getMachineId())
                    .ifPresent(conv::setContextMachine);
        }
        if (request.getWorkOrderId() != null) {
            workOrderRepository.findById(request.getWorkOrderId())
                    .ifPresent(conv::setContextWorkOrder);
        }

        return conversationRepository.save(conv);
    }

    private void saveMessage(ChatConversation conversation, ChatSender sender, String text) {
        ChatMessage msg = new ChatMessage();
        msg.setConversation(conversation);
        msg.setSender(sender);
        msg.setMessageText(text);
        msg.setCreatedAt(Instant.now());
        messageRepository.save(msg);
    }

    private String buildContext(AskRequest request) {
        StringBuilder ctx = new StringBuilder();

        if (request.getMachineId() != null) {
            machineRepository.findById(request.getMachineId()).ifPresent(machine -> {
                ctx.append("Machine: ").append(machine.getName())
                        .append(" | Type: ").append(machine.getMachineType())
                        .append(" | Location: ").append(machine.getLocation())
                        .append(" | Status: ").append(machine.getStatus())
                        .append(" | Criticality: ").append(machine.getCriticality())
                        .append("\n");
            });
        }

        if (request.getWorkOrderId() != null) {
            workOrderRepository.findById(request.getWorkOrderId()).ifPresent(wo -> {
                ctx.append("Work Order: ").append(wo.getTitle())
                        .append(" | Priority: ").append(wo.getPriority())
                        .append(" | Status: ").append(wo.getStatus())
                        .append("\n");
                if (wo.getDescription() != null) {
                    ctx.append("Problem: ").append(wo.getDescription()).append("\n");
                }
            });
        }

        return ctx.toString();
    }

    private String callGroq(List<ChatMessage> history, String context) throws Exception {
        String systemPrompt = "You are an expert industrial maintenance assistant for MiniMaxi platform.\n"
                + "Be concise, practical, and safety-aware.\n"
                + "Reply in the same language as the technician's message (Arabic or English).\n"
                + (context.isEmpty() ? "" : "\nContext about current machine/work order:\n" + context);

        // ابني الـ messages list: system + كل الـ history
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));

        for (ChatMessage msg : history) {
            String role = msg.getSender() == ChatSender.USER ? "user" : "assistant";
            messages.add(Map.of("role", role, "content", msg.getMessageText()));
        }

        String requestBody = objectMapper.writeValueAsString(Map.of(
                "model", "llama-3.1-8b-instant",
                "max_tokens", 500,
                "messages", messages
        ));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + groqApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(httpRequest,
                HttpResponse.BodyHandlers.ofString());

        JsonNode root = objectMapper.readTree(response.body());
        return root.path("choices").get(0)
                .path("message").path("content").asText();
    }



    @Override
    public List<ConversationResponse> getConversations(Long userId) {
        return conversationRepository.findByUserIdOrderByLastMessageAtDesc(userId)
                .stream()
                .map(conv -> ConversationResponse.builder()
                        .id(conv.getId())
                        .machineId(conv.getContextMachine() != null ? conv.getContextMachine().getId() : null)
                        .machineName(conv.getContextMachine() != null ? conv.getContextMachine().getName() : null)
                        .workOrderId(conv.getContextWorkOrder() != null ? conv.getContextWorkOrder().getId() : null)
                        .workOrderTitle(conv.getContextWorkOrder() != null ? conv.getContextWorkOrder().getTitle() : null)
                        .status(conv.getStatus().name().toLowerCase())
                        .createdAt(conv.getCreatedAt())
                        .lastMessageAt(conv.getLastMessageAt())
                        .build())
                .toList();
    }

    @Override
    public List<ChatMessageResponse> getMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(msg -> ChatMessageResponse.builder()
                        .id(msg.getId())
                        .sender(msg.getSender().name())
                        .message(msg.getMessageText())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .toList();
    }
}