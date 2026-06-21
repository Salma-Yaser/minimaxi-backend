package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {

    List<UserDeviceToken> findByUserId(Long userId);

    Optional<UserDeviceToken> findByDeviceToken(String deviceToken);
}
