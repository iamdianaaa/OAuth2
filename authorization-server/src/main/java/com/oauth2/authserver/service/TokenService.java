package com.oauth2.authserver.service;

import com.oauth2.authserver.model.AccessToken;
import com.oauth2.authserver.model.User;
import com.oauth2.authserver.repository.AccessTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class TokenService {
    @Autowired
    private AccessTokenRepository tokenRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public AccessToken createToken(User user) {
        String tokenString = jwtUtil.generateToken(user.getUsername());
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtUtil.getExpirationTime());
        
        AccessToken token = new AccessToken(tokenString, user, expiresAt);
        return tokenRepository.save(token);
    }
    
    public boolean validateToken(String tokenString) {
        return tokenRepository.findByToken(tokenString)
                .map(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
    
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
