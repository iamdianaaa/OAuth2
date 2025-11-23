package com.oauth2.authserver.repository;

import com.oauth2.authserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

// src/main/java/com/oauth2/authserver/repository/AccessTokenRepository.java
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