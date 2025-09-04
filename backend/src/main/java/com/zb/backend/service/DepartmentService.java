package com.zb.backend.service;

import com.zb.backend.constants.enums.DepartmentEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Department;
import com.zb.backend.entity.Employee;
import com.zb.backend.mapper.DepartmentMapper;
import com.zb.backend.mapper.EmployeeMapper;
import com.zb.backend.model.request.DeptRequest;
import com.zb.backend.model.response.GetAllDepartmentResponse;
import com.zb.backend.model.response.SimpleDepartmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentMapper departmentMapper;
    private final EmployeeMapper employeeMapper;

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

    // 获取所有部门信息
    public List<GetAllDepartmentResponse> getAllDept() {
        return departmentMapper.selectAllDept();
    }

    // 新增部门
    @Transactional(rollbackFor = Exception.class)
    public ResultEnum addDept(DeptRequest request) {
        // 首先判断部门名称是否存在
        String deptName = request.getDeptName();
        Long managerId = request.getManagerId();

        Department existsDept = departmentMapper.selectByName(deptName);
        if (existsDept != null) throw new RuntimeException(DepartmentEnum.ERR_DEPT_NAME.getMessage());

        // 新增部门
        Boolean addDept = departmentMapper.insertDept(request);
        if (!addDept) throw new RuntimeException(DepartmentEnum.ERR_ADD_DEPT.getMessage());

        // 获取部门信息，通过Name
        Department newDept = departmentMapper.selectByName(deptName);
        Long deptId = newDept.getDeptId();

        // 如果未查询到部门，说明部门名称不重复
        /*
        *
        * 部门插入/修改逻辑
        * 当存在managerId的时候，需要把这个员工，设为经理，并把职位改成XX部经理
        * 修改的时候，如果部门已存在经理，那么把原来那个经理变为普通员工，把新的id设为经理，职位改成XX部经理
        * TODO 可考虑修改职位发通知
        * 新增和修改的时候，如果设置的managerId，之前已经是管理员，那么需要把对应部门的managerId设为null
        *
        *  */

        // 首先给设置的部门经理更改职位和isManager
        if (managerId != null) {
            // 首先判断该员工是否存在
            Employee employee = employeeMapper.selectEmployeeByEmpId(managerId);
            if (employee == null) throw new RuntimeException(DepartmentEnum.ERR_EMP_ID.getMessage());
            // 判断员工之前是否为经理，如果是，把之前的部门managerId改为null
            if (employee.getIsManager()) {
                // 修改之前部门
                Boolean updateManagerId = departmentMapper.updateManagerIdByDeptId(employee.getDeptId());
                if (!updateManagerId) throw new RuntimeException(DepartmentEnum.ERR_ADD_DEPT.getMessage());
            }
            // 如果存在，执行修改操作
            Boolean updateEmpDept = employeeMapper.updateEmpDept(managerId, deptId, deptName + "经理");
            if (!updateEmpDept) throw new RuntimeException(DepartmentEnum.ERR_ADD_DEPT.getMessage());
        }


        return DepartmentEnum.SUC_ADD_DEPT;
    }

    // 修改部门
    @Transactional(rollbackFor = Exception.class)
    public ResultEnum updateDept(@Valid DeptRequest request) {
        Long deptId = request.getDeptId();
        String newDeptName = request.getDeptName();
        Long newManagerId = request.getManagerId();

        // 判断新的员工id是否存在
        Employee employee = employeeMapper.selectEmployeeByEmpId(newManagerId);
        if (employee == null) throw new RuntimeException(DepartmentEnum.ERR_EMP_ID.getMessage());

        // 首先根据部门id查询部门
        Department oldDept = departmentMapper.selectDepartmentByDeptId(deptId);
        // 如果为空
        if (oldDept == null) throw new RuntimeException(DepartmentEnum.ERR_EXISTS_DEPT.getMessage());
        String oldDeptName = oldDept.getDeptName();
        Long oldManagerId = oldDept.getManagerId();

        // 不为空，判断一下部门名称和部门经理是否和旧数据一样，如果一样，可以直接更新
        if (!oldDeptName.equals(newDeptName)) {
            // 新旧名字不一样，根据名字去查数据库，看看能不能查得到
            Department existsDept = departmentMapper.selectByName(newDeptName);
            if (existsDept != null) throw new RuntimeException(DepartmentEnum.ERR_DEPT_NAME.getMessage());
        }

        // 如果原本managerId为空，可以直接进行修改
        if (oldManagerId == null) {
            Boolean updateNew = employeeMapper.updateEmpDept(newManagerId, deptId, newDeptName + "经理");
            if (!updateNew) throw new RuntimeException(DepartmentEnum.ERR_UPDATE_DEPT.getMessage());
        }

        // 如果ManagerId和旧的不一样
        else if (!oldManagerId.equals(newManagerId)) {
                        // 这里还需要获取旧员工的信息，判断旧员工之前是否为管理
            Employee oldEmp = employeeMapper.selectEmployeeByEmpId(oldManagerId);
            if (oldEmp.getIsManager()) {
                // 修改之前部门
                Boolean updateManagerId = departmentMapper.updateManagerIdByDeptId(oldEmp.getDeptId());
                if (!updateManagerId) throw new RuntimeException(DepartmentEnum.ERR_ADD_DEPT.getMessage());
            }

            // 如果新员工存在，那么修改新员工的部门信息和旧员工的部门信息
            // 修改旧员工
            Boolean updateOld = employeeMapper.updateEmpDept(oldManagerId, null, "普通员工");
            // if (!updateOld) throw new RuntimeException(DepartmentEnum.ERR_UPDATE_DEPT.getMessage());
            // 修改新员工
            Boolean updateNew = employeeMapper.updateEmpDept(newManagerId, deptId, newDeptName + "经理");
            if (!updateOld || !updateNew) throw new RuntimeException(DepartmentEnum.ERR_UPDATE_DEPT.getMessage());
        }

        // 修改完成后，如果原来的员工是经理，那么需要将对应的部门的managerId改为null
        // 判断员工之前是否为经理，如果是，把之前的部门managerId改为null
        if (employee.getIsManager()) {
            // 修改之前部门
            Boolean updateManagerId = departmentMapper.updateManagerIdByDeptId(employee.getDeptId());
            if (!updateManagerId) throw new RuntimeException(DepartmentEnum.ERR_ADD_DEPT.getMessage());
        }

        // 其他问题解决，开始修改部门信息
        Boolean updateDept = departmentMapper.updateDept(request);
        if (!updateDept) throw new RuntimeException(DepartmentEnum.ERR_UPDATE_DEPT.getMessage());

        return DepartmentEnum.SUC_UPDATE_DEPT;
    }
}
