package com.zb.backend.mapper;

import com.zb.backend.entity.Department;
import com.zb.backend.model.request.DeptRequest;
import com.zb.backend.model.response.GetAllDepartmentResponse;
import com.zb.backend.model.response.SimpleDepartmentResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface DepartmentMapper {
    // 根据部门ID查询部门信息
    Department selectDepartmentByDeptId(Long deptId);

    // 检查部门是否存在
    Boolean existsByDeptId(Long deptId);

    // 查询简单部门
    List<SimpleDepartmentResponse> selectSimpleDepartment();

    // 获取所有部门信息
    List<GetAllDepartmentResponse> selectAllDept();

    // 根据部门名称查找部门
    Department selectByName(String deptName);

    // 新增部门
    Boolean insertDept(DeptRequest request);

    // 修改部门
    Boolean updateDept(@Valid DeptRequest request);

    // 根据部门id修改managerId为null
    Boolean updateManagerIdByDeptId(Long deptId);
}
