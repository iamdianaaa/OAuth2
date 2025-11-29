package com.oauth2.client.controller;

import com.oauth2.client.dto.LoginRequest;
import com.oauth2.client.dto.TokenResponse;
import com.oauth2.client.service.OAuth2ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private OAuth2ClientService clientService;
    
    private String currentToken = null;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            TokenResponse tokenResponse = clientService.login(
                request.getUsername(), 
                request.getPassword()
            );
            
            currentToken = tokenResponse.getAccessToken();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", tokenResponse.getAccessToken());
            response.put("expiresIn", tokenResponse.getExpiresIn());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/access-resource")
    public ResponseEntity<?> accessResource(@RequestParam(required = false) String token) {
        try {
            String accessToken = token != null ? token : currentToken;
            
            if (accessToken == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "No token available. Please login first."));
            }
            
            Map<String, Object> resource = clientService.accessProtectedResource(
                accessToken, 
                "/api/resources/protected/data"
            );
            
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "Access denied: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(@RequestParam(required = false) String token) {
        try {
            String accessToken = token != null ? token : currentToken;
            
            if (accessToken == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "No token available. Please login first."));
            }
            
            Map<String, Object> userInfo = clientService.accessProtectedResource(
                accessToken, 
                "/api/resources/protected/user-info"
            );
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "Access denied: " + e.getMessage()));
        }
    }
    
    @PostMapping("/perform-action")
    public ResponseEntity<?> performAction(@RequestParam(required = false) String token,
                                           @RequestBody Map<String, Object> payload) {
        try {
            String accessToken = token != null ? token : currentToken;
            
            if (accessToken == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "No token available. Please login first."));
            }
            
            Map<String, Object> result = clientService.postToProtectedResource(
                accessToken, 
                "/api/resources/protected/action",
                payload
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "Action failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("clientActive", true);
        status.put("hasToken", currentToken != null);
        status.put("message", "Client application is running");
        
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        currentToken = null;
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}