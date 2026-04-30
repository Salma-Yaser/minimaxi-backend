package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetTypeRepository extends JpaRepository<AssetType, Long> {
}