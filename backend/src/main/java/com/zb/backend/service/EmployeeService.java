package com.zb.backend.service;

import com.zb.backend.constants.enums.AuthEnum;
import com.zb.backend.constants.enums.EmployeeEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Employee;
import com.zb.backend.entity.MeetingRoom;
import com.zb.backend.mapper.EmployeeMapper;
import com.zb.backend.model.PageResult;
import com.zb.backend.model.request.QueryEmployeesRequest;
import com.zb.backend.model.request.RegisterRequest;
import com.zb.backend.model.request.UpdateEmployeeInfo;
import com.zb.backend.model.response.QueryEmployeesResponse;
import com.zb.backend.util.PaginationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeMapper employeeMapper;

    // 通过id获取员工信息
    public Employee getEmployeeByEmpId(Long empId) {
        System.out.println(this.getClass().getSimpleName() + " 需要获取员工信息的账号：" + empId);
        return employeeMapper.selectEmployeeByEmpId(empId);
    }

    // 判断手机号码是否存在
    public Boolean existsPhone(String phone) {
        return employeeMapper.existsByPhone(phone);
    }

    // 判断身份证号是否存在
    public Boolean existsIdCard(String idCard) {
        return employeeMapper.existsByIdCard(idCard);
    }

    // 新增员工信息
    public Boolean addEmployee(RegisterRequest registerRequest) {
        return employeeMapper.insertEmployee(registerRequest);
    }

    // 分页查询员工所有信息
    public PageResult<QueryEmployeesResponse> queryEmployees(QueryEmployeesRequest queryRequest) {

        // 查询总记录数
        Integer total = employeeMapper.countEmployeeList(queryRequest);

        // 分页校验工具类
        PageResult<QueryEmployeesResponse> pageResult = PaginationValidator.validatePagination(queryRequest, total);
        if (pageResult != null) {
            return pageResult;
        }

        // 记录数不为0，根据筛选条件查询
        List<QueryEmployeesResponse> employeesResponseList = employeeMapper.selectEmployeeList(queryRequest);

        return new PageResult<>(total, queryRequest.getPageNum(), queryRequest.getPageSize(), employeesResponseList);
    }

    // 更新员工信息
    public ResultEnum updateEmployeeInfo(@Valid UpdateEmployeeInfo updateEmployeeInfo) {
        // 首先通过id，查询原数据是否正常，经理数据
        Employee employee = employeeMapper.selectEmployeeByEmpId(updateEmployeeInfo.getEmpId());

        // 当修改的员工为经理是，部门ID、职位不能修改，
        if (employee.getIsManager()) {
            if (
                    !employee.getEmpId().equals(updateEmployeeInfo.getEmpId())
                            ||
                            !employee.getPosition().equals(updateEmployeeInfo.getPosition())) {
                return EmployeeEnum.ERR_UPDATE_MANAGER;
            }
        }

        // 当修改为员工时，职位不能带有“经理”、“管理”字样
        if (!employee.getIsManager()) {
            if (updateEmployeeInfo.getPosition().contains("经理") || updateEmployeeInfo.getPosition().contains("管理")) {
                return EmployeeEnum.ERR_UPDATE_POSITION;
            }
        }

        // 判断手机号是否存在，如果修改的手机号和当前手机号不一样
        if (!employee.getPhone().equals(updateEmployeeInfo.getPhone())) {
            if (existsPhone(updateEmployeeInfo.getPhone())) {
                return EmployeeEnum.ERR_PHONE_DUPLICATE;
            }
        }

        // 判断身份证号是否存在，如果修改的身份证和当前身份证不一样
        if (!employee.getIdCard().equals(updateEmployeeInfo.getIdCard())) {
            if (existsIdCard(updateEmployeeInfo.getIdCard())) {
                return EmployeeEnum.ERR_IDCARD_DUPLICATE;
            }
        }

        Boolean updateInfo = employeeMapper.updateEmployeeInfo(updateEmployeeInfo);

        if (!updateInfo) {
            return EmployeeEnum.ERR_UPDATE_INFO;
        }

        return EmployeeEnum.SUC_UPDATE_INFO;
    }
}
