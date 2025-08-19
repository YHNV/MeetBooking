package com.zb.backend.entity;

import com.zb.backend.entity.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notification {
    private Long notificationId;
    private Long senderId;
    private Long receiverId;
    private NotificationType notificationType;
    private String title;
    private String content;
    private Long relatedId;
    private Boolean isRead;
    private LocalDateTime createTime;
    private LocalDateTime readTime;
}