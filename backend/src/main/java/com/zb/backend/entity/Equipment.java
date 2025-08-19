package com.zb.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Equipment {
    private Long equipmentId;
    private String equipmentName;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
