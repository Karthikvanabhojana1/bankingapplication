package com.banking.notificationservice.service.impl;

import com.banking.notificationservice.dto.NotificationDto;
import com.banking.notificationservice.entity.Notification;
import com.banking.notificationservice.entity.NotificationType;
import com.banking.notificationservice.repository.NotificationRepository;
import com.banking.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository repository;

    @Override
    @Transactional
    public NotificationDto send(NotificationDto notificationDto) {
        Notification notification = toEntity(notificationDto);
        Notification saved = repository.save(notification);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDto getById(Long id) {
        Notification notification = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        return toDto(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getByUserId(Long userId) {
        return repository.findByUserId(userId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getByType(NotificationType type) {
        return repository.findByType(type).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationDto markAsRead(Long id) {
        Notification notification = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        notification.setRead(true);
        Notification updated = repository.save(notification);
        return toDto(updated);
    }

    private NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setUserId(n.getUserId());
        dto.setType(n.getType());
        dto.setSubject(n.getSubject());
        dto.setMessage(n.getMessage());
        dto.setRecipient(n.getRecipient());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setReadAt(n.getReadAt());
        return dto;
    }

    private Notification toEntity(NotificationDto dto) {
        Notification n = new Notification();
        n.setUserId(dto.getUserId());
        n.setType(dto.getType());
        n.setSubject(dto.getSubject());
        n.setMessage(dto.getMessage());
        n.setRecipient(dto.getRecipient());
        n.setRead(dto.isRead());
        n.setCreatedAt(dto.getCreatedAt());
        n.setReadAt(dto.getReadAt());
        return n;
    }
}


