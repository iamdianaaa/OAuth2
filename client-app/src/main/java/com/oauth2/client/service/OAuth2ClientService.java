package com.oauth2.client.service;

import com.oauth2.client.dto.LoginRequest;
import com.oauth2.client.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class OAuth2ClientService {
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${auth.server.url}")
    private String authServerUrl;
    
    @Value("${resource.server.url}")
    private String resourceServerUrl;
    
    public TokenResponse login(String username, String password) {
        String url = authServerUrl + "/api/auth/login";
        
        LoginRequest request = new LoginRequest(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<TokenResponse> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            TokenResponse.class
        );
        
        return response.getBody();
    }
    
    public Map<String, Object> accessProtectedResource(String token, String endpoint) {
        String url = resourceServerUrl + endpoint;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map.class
        );
        
        return response.getBody();
    }
    
    public Map<String, Object> postToProtectedResource(String token, 
                                                        String endpoint, 
                                                        Map<String, Object> payload) {
        String url = resourceServerUrl + endpoint;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            Map.class
        );
        
        return response.getBody();
    }
}