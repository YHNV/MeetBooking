package com.zb.backend.mapper;

public interface NotificationMapper {
    // 添加通知
    Boolean insertNotification(Long senderId, Long receiverId, String notificationType, String title, String content, Long relatedId);
}
