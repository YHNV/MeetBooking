package com.zb.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

// 部门表实体类
@Data
public class Department {
    private Long deptId;
    private String deptName;
    private Long managerId;
    private String deptAddress;
    private String deptDesc;
    private Boolean isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
