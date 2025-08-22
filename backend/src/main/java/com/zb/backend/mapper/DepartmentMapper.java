package com.zb.backend.mapper;

import com.zb.backend.entity.Department;
import com.zb.backend.model.response.SimpleDepartmentResponse;

import java.util.List;

public interface DepartmentMapper {
    // 根据部门ID查询部门信息
    Department selectDepartmentByDeptId(Long deptId);

    // 检查部门是否存在
    Boolean existsByDeptId(Long deptId);

    // 查询简单部门
    List<SimpleDepartmentResponse> selectSimpleDepartment();
}
