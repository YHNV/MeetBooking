package com.zb.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Account {
    private Long accountId;
    private String password;
    private Boolean isAdmin;
    private Boolean isActive;
    private Boolean firstLogin;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}