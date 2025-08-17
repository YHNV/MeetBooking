package com.zb.backend.mapper;

import com.zb.backend.entity.Department;

public interface DepartmentMapper {
    // 根据部门ID查询部门信息
    Department selectDepartmentByDeptId(Long deptId);

    // 检查部门是否存在
    Boolean existsByDeptId(Long deptId);
}
