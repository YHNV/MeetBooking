package com.zb.backend.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetAllDepartmentResponse {
    private Long deptId;
    private String deptName;
    private Long managerId;
    private String empName;
    private String deptAddress;
    private String deptDesc;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
