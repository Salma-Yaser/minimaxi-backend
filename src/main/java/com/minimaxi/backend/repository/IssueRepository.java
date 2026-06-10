package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByMachineIdOrderByCreatedAtDesc(Long machineId);
}