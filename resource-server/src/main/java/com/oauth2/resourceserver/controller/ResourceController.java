package com.oauth2.resourceserver.controller;

import com.oauth2.resourceserver.dto.ResourceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @GetMapping("/public/info")
    public ResponseEntity<?> getPublicInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a public resource, no authentication required");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected/data")
    public ResponseEntity<?> getProtectedData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        ResourceResponse response = new ResourceResponse(
                "This is a protected resource. Access granted!",
                username
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected/user-info")
    public ResponseEntity<?> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", username);
        userInfo.put("message", "User information retrieved successfully");
        userInfo.put("authorities", auth.getAuthorities());

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/protected/action")
    public ResponseEntity<?> performAction(@RequestBody Map<String, Object> payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Action performed successfully");
        response.put("user", username);
        response.put("payload", payload);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected/admin")
    public ResponseEntity<?> getAdminData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin resource accessed");
        response.put("user", username);
        response.put("adminData", "Sensitive administrative information");

        return ResponseEntity.ok(response);
    }
}