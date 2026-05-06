package com.minimaxi.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimaxi.backend.dto.request.AskRequest;
import com.minimaxi.backend.dto.response.AskResponse;
import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.entity.WorkOrder;
import com.minimaxi.backend.repository.MachineRepository;
import com.minimaxi.backend.repository.SensorReadingRepository;
import com.minimaxi.backend.repository.WorkOrderRepository;
import com.minimaxi.backend.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final MachineRepository machineRepository;
    private final WorkOrderRepository workOrderRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiServiceImpl(MachineRepository machineRepository,
                         WorkOrderRepository workOrderRepository,
                         SensorReadingRepository sensorReadingRepository) {
        this.machineRepository = machineRepository;
        this.workOrderRepository = workOrderRepository;
        this.sensorReadingRepository = sensorReadingRepository;
    }

    @Override
    public AskResponse ask(AskRequest request) {
        try {
            String context = buildContext(request);
            String reply = callGroq(request.getMessage(), context);
            return new AskResponse(reply);
        } catch (Exception e) {
            throw new RuntimeException("AI service temporarily unavailable");
        }
    }

    private String buildContext(AskRequest request) {
        StringBuilder ctx = new StringBuilder();

        // Machine context
        if (request.getMachineId() != null) {
            machineRepository.findById(request.getMachineId()).ifPresent(machine -> {
                ctx.append("Machine: ").append(machine.getName())
                        .append(" | Type: ").append(machine.getMachineType())
                        .append(" | Location: ").append(machine.getLocation())
                        .append(" | Status: ").append(machine.getStatus())
                        .append(" | Criticality: ").append(machine.getCriticality())
                        .append("\n");

                // Latest sensor readings
                var readings = sensorReadingRepository
                        .findBySensorMachineIdOrderByReadingTimeDesc(machine.getId());
                if (!readings.isEmpty()) {
                    ctx.append("Latest Sensor Readings:\n");
                    readings.stream().limit(5).forEach(r ->
                            ctx.append("  - ").append(r.getSensor().getSensorType().getName())
                                    .append(": ").append(r.getValue())
                                    .append(" ").append(r.getSensor().getSensorType().getUnit())
                                    .append("\n")
                    );
                }
            });
        }

        // Work order context
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

    private String callGroq(String message, String context) throws Exception {
        String systemPrompt = """
                You are an expert industrial maintenance assistant for MiniMaxi platform.
                Be concise, practical, and safety-aware.
                Reply in the same language as the technician's message (Arabic or English).
                """;

        String userContent = context.isEmpty()
                ? message
                : "Context:\n" + context + "\nTechnician Question: " + message;

        String requestBody = objectMapper.writeValueAsString(
                new java.util.HashMap<>() {{
                    put("model", "llama-3.1-8b-instant");
                    put("max_tokens", 500);
                    put("messages", List.of(
                            new java.util.HashMap<>() {{
                                put("role", "system");
                                put("content", systemPrompt);
                            }},
                            new java.util.HashMap<>() {{
                                put("role", "user");
                                put("content", userContent);
                            }}
                    ));
                }}
        );

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
}