package com.minimaxi.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PersonRefResponse {
    private Long id;
    private String name;
}