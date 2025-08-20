package com.zb.backend.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueryEmployeesResponse {
    private Long accountId;
    private Boolean isActive;
    private LocalDateTime lastLoginTime;

    private Long empId;
    private String empName;
    private String position;
    private String phone;
    private String email;
    private String idCard;
    private Boolean isManager;
    private LocalDateTime createTime;

    private Long deptId;
    private String deptName;

}
