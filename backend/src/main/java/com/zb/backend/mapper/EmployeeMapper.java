package com.zb.backend.mapper;

import com.zb.backend.entity.Employee;
import com.zb.backend.model.request.QueryEmployeesRequest;
import com.zb.backend.model.request.RegisterRequest;
import com.zb.backend.model.request.UpdateEmployeeInfo;
import com.zb.backend.model.response.QueryEmployeesResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface EmployeeMapper {
    // 通过id获取员工信息
    Employee selectEmployeeByEmpId(Long empId);

    // 判断电话号码是否存在
    Boolean existsByPhone(String phone);

    // 判断身份证号是否存在
    Boolean existsByIdCard(String idCard);

    // 新增员工信息
    Boolean insertEmployee(RegisterRequest registerRequest);

    // 查询总记录数
    Integer countEmployeeList(QueryEmployeesRequest queryRequest);

    // 分页查询员工所有信息
    List<QueryEmployeesResponse> selectEmployeeList(QueryEmployeesRequest queryRequest);

    Boolean updateEmployeeInfo(@Valid UpdateEmployeeInfo updateEmployeeInfo);
}
