package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    List<ChatConversation> findByUserIdOrderByLastMessageAtDesc(Long userId);

    List<ChatConversation> findByOrganizationId(Long organizationId);
}
