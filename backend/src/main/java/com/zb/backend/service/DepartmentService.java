package com.zb.backend.service;

import com.zb.backend.entity.Department;
import com.zb.backend.mapper.DepartmentMapper;
import com.zb.backend.model.response.SimpleDepartmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentMapper departmentMapper;

    // 通过id获取部门信息
    public Department getDepartmentByDeptId(Long deptId) {
        return departmentMapper.selectDepartmentByDeptId(deptId);
    }

    // 通过id查询部门是否存在
    public Boolean existsByDeptId(Long deptId) {
        return departmentMapper.existsByDeptId(deptId);
    }

    // 获取简单部门集合
    public List<SimpleDepartmentResponse> getSimpleDepartmentList() {
        return departmentMapper.selectSimpleDepartment();
    }
}
