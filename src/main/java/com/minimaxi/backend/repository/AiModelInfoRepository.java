package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.AiModelInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiModelInfoRepository extends JpaRepository<AiModelInfo, Long> {
    List<AiModelInfo> findByOrganizationId(Long organizationId);
}