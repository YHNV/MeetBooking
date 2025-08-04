package com.zb.backend.model.response;

import lombok.Data;

import java.time.LocalDateTime;

// 登录响应模型
@Data
public class LoginResponse {
    private Boolean isAdmin;
    private Boolean isManager;
    private Boolean firstLogin;

    private LocalDateTime lastLoginTime;

    private Long deptId;
    private String deptName;

    private Long empId;
    private String empName;
    private String position;
    private String phone;
    private String email;

    private String token;

}
