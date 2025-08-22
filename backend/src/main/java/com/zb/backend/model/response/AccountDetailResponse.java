package com.zb.backend.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountDetailResponse {
    // 账号相关
    private Long accountId;
    private Boolean isAdmin;
    private Boolean isActive;
    private Boolean firstLogin;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 员工信息相关
    private Long empId;
    private String empName;
    private String position;
    private String phone;
    private String email;
    private String idCard;
    private Boolean isManager;

    // 部门相关
    private Long deptId;
    private String deptName;
    private Long managerId;
    private String deptAddress;
    private String deptDesc;

    // 身份证相关
    private Integer age;                 // 年龄
    private String gender;               // 性别
    private String formattedBirthday;    // 带年份的出生日期
}
