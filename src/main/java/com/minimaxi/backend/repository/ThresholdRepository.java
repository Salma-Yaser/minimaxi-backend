package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.Threshold;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThresholdRepository extends JpaRepository<Threshold, Long> {
}