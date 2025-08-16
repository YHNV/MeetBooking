package com.zb.backend.model.response;

import lombok.Data;

import java.time.LocalDateTime;

// 登录响应模型
// 返回首页基本信息就行了，个人详情界面再返回详细数据
@Data
public class LoginResponse {
    private Long accountId;
    private Boolean isAdmin;
    private Boolean isManager;
    private Boolean firstLogin;

    private LocalDateTime lastLoginTime;

    private String deptName;
    private String position;

    private Long empId;
    private String empName;

    private String token;

}
