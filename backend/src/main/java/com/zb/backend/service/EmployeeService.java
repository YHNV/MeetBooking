package com.zb.backend.service;

import com.zb.backend.constants.enums.AuthEnum;
import com.zb.backend.constants.enums.EmployeeEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Employee;
import com.zb.backend.mapper.EmployeeMapper;
import com.zb.backend.model.PageResult;
import com.zb.backend.model.request.QueryEmployeesRequest;
import com.zb.backend.model.request.RegisterRequest;
import com.zb.backend.model.request.UpdateEmployeeInfo;
import com.zb.backend.model.response.QueryEmployeesResponse;
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
        // 校验分页参数
        // 当PageNum为空是，默认为1
        if (queryRequest.getPageNum() == null || queryRequest.getPageNum() < 1) {
            queryRequest.setPageNum(1);
        }
        // 当PageSize为空或超过50时，设置为10
        if (queryRequest.getPageSize() == null || queryRequest.getPageSize() < 1 || queryRequest.getPageSize() > 50) {
            // 防止 pageSize 过大
            queryRequest.setPageSize(10);
        }

        // 查询总记录数
        Integer total = employeeMapper.countEmployeeList(queryRequest);

        // this.pages = (int) Math.ceil((double) total / pageSize);
        // if (pageNum >= pages) {
        //     this.pageNum = pages;
        // }

        // 如果当前页参数大于总页数，那么将其改为第一页
        if (queryRequest.getPageNum() >= ((int) Math.ceil((double) total / queryRequest.getPageSize()))) {
            queryRequest.setPageNum(1);
        }

        // 如果记录数为0，直接返回结果
        if (total == 0) {
            return new PageResult<>(0, queryRequest.getPageNum(), queryRequest.getPageSize(), List.of());
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
