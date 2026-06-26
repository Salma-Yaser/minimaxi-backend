package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.request.AskRequest;
import com.minimaxi.backend.dto.response.AskResponse;
import com.minimaxi.backend.service.AiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public AskResponse ask(@RequestBody AskRequest request) {
        return aiService.ask(request);
    }
}