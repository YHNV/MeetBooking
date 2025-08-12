package com.zb.backend.entity;

import lombok.Data;
import java.time.Instant;

@Data
public class Token {
    private Long id;            // 自增主键
    private String token;       // JWT Token
    private Long accountId;     // 用户 ID（对应 Account 表的 account_id）
    private Instant expiryTime; // 过期时间（Instant 或 LocalDateTime 均可）
}