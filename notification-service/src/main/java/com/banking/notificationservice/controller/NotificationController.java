package com.banking.notificationservice.controller;

import com.banking.common.dto.ApiResponse;
import com.banking.notificationservice.dto.NotificationDto;
import com.banking.notificationservice.entity.NotificationType;
import com.banking.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationDto>> send(@Valid @RequestBody NotificationDto notificationDto) {
        try {
            NotificationDto saved = notificationService.send(notificationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Notification sent", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationDto>> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Notification retrieved", notificationService.getById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notificationService.getByUserId(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getByType(@PathVariable NotificationType type) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notificationService.getByType(type)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Notification marked as read", notificationService.markAsRead(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}


