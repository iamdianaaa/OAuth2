package com.oauth2.authserver.controller;

import com.oauth2.authserver.dto.LoginRequest;
import com.oauth2.authserver.dto.RegisterRequest;
import com.oauth2.authserver.dto.TokenResponse;
import com.oauth2.authserver.model.AccessToken;
import com.oauth2.authserver.model.User;
import com.oauth2.authserver.service.TokenService;
import com.oauth2.authserver.service.UserService;
import com.oauth2.authserver.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                request.getUsername(),
                request.getPassword(),
                request.getEmail()
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("username", user.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (userService.authenticate(request.getUsername(), request.getPassword())) {
            User user = userService.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AccessToken token = tokenService.createToken(user);
            TokenResponse response = new TokenResponse(
                token.getToken(),
                jwtUtil.getExpirationTime()
            );
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = tokenService.validateToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            
            if (isValid) {
                String username = jwtUtil.extractUsername(token);
                response.put("username", username);
            }
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
    }
}