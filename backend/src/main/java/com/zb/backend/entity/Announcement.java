package com.zb.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Announcement {
    private Long announcementId;
    private Long senderId;
    private String title;
    private String content;
    private Boolean isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
