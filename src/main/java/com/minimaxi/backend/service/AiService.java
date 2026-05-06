package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.request.AskRequest;
import com.minimaxi.backend.dto.response.AskResponse;

public interface AiService {
    AskResponse ask(AskRequest request);
}