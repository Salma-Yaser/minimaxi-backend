package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.request.AskRequest;
import com.minimaxi.backend.dto.response.AskResponse;
import com.minimaxi.backend.scheduler.PredictionScheduler;
import com.minimaxi.backend.service.AiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final AiService aiService;
    private final PredictionScheduler predictionScheduler;

    public AiController(AiService aiService, PredictionScheduler predictionScheduler) {
        this.aiService = aiService;
        this.predictionScheduler = predictionScheduler;
    }

    @PostMapping("/ask")
    public AskResponse ask(@RequestBody AskRequest request) {
        return aiService.ask(request);
    }

    @PostMapping("/run-predictions")
    public String runPredictions() {
        predictionScheduler.runPredictions();
        return "Predictions triggered manually";
    }
}