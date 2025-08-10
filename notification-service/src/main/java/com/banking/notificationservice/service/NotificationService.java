package com.banking.notificationservice.service;

import com.banking.notificationservice.dto.NotificationDto;
import com.banking.notificationservice.entity.NotificationType;

import java.util.List;

public interface NotificationService {
    NotificationDto send(NotificationDto notificationDto);
    NotificationDto getById(Long id);
    List<NotificationDto> getByUserId(Long userId);
    List<NotificationDto> getByType(NotificationType type);
    NotificationDto markAsRead(Long id);
}


