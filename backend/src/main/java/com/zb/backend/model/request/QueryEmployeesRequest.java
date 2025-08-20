package com.zb.backend.model.request;

import lombok.Data;

@Data
public class QueryEmployeesRequest {
    // 分页参数
    private Integer pageNum = 1; // 默认第一页
    private Integer pageSize = 10; // 默认查询10条
    // 查询条件，为空默认查询所有
    private Long deptId; // 部门ID
    private Boolean isActive; // 是否启用
    private Boolean isManager; // 是否为管理员
    private String empName; // 模糊查询员工姓名

}
