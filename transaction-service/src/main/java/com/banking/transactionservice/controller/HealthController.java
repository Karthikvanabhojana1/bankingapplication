package com.banking.transactionservice.controller;

import com.banking.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("service", "Transaction Service");
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        
        // Check database connectivity
        try (Connection connection = dataSource.getConnection()) {
            healthInfo.put("database", "UP");
            healthInfo.put("databaseUrl", connection.getMetaData().getURL());
        } catch (Exception e) {
            healthInfo.put("database", "DOWN");
            healthInfo.put("databaseError", e.getMessage());
            healthInfo.put("status", "DEGRADED");
        }
        
        return ResponseEntity.ok(ApiResponse.success("Health check completed", healthInfo));
    }
    
    @GetMapping("/readiness")
    public ResponseEntity<ApiResponse<Map<String, Object>>> readinessCheck() {
        Map<String, Object> readinessInfo = new HashMap<>();
        readinessInfo.put("service", "Transaction Service");
        readinessInfo.put("status", "READY");
        readinessInfo.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.success("Readiness check completed", readinessInfo));
    }
    
    @GetMapping("/liveness")
    public ResponseEntity<ApiResponse<Map<String, Object>>> livenessCheck() {
        Map<String, Object> livenessInfo = new HashMap<>();
        livenessInfo.put("service", "Transaction Service");
        livenessInfo.put("status", "ALIVE");
        livenessInfo.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.success("Liveness check completed", livenessInfo));
    }
}
