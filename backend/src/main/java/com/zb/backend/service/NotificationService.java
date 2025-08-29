package com.zb.backend.service;

import com.zb.backend.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationMapper notificationMapper;

    // 添加通知
    public Boolean addNotification(Long senderId, Long accountId, String notificationType, String title, String content, Long reservationId) {
        return notificationMapper.insertNotification(senderId, accountId, notificationType, title, content, reservationId);
    }
}
