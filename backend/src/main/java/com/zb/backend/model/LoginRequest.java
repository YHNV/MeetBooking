package com.zb.backend.model;

import lombok.Data;

// 登录请求模型
@Data
public class LoginRequest {
    private String username;
    private String password;
}
