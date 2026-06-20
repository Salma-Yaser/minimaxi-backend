package com.minimaxi.backend.dto.request;

import lombok.Data;

@Data
public class RateWorkOrderRequest {

    // اليوزر اللي بيقيّم (المفروض الـ engineer/creator اللي استلم الـ notification)
    private Long ratedByUserId;

    // 1-5
    private Integer stars;

    private String feedback;
}