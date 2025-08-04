package com.zb.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Employee {
    private Long empId;
    private String empName;
    private Long deptId;
    private String position;
    private String phone;
    private String email;
    private String idCard;
    private Boolean isManager;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
