package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
}