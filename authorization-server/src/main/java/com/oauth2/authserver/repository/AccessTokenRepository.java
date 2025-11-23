package com.oauth2.authserver.repository;

import com.oauth2.authserver.model.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    Optional<AccessToken> findByToken(String token);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}