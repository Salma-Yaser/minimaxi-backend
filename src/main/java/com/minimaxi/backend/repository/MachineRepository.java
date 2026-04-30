package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<Machine, Long> {
}