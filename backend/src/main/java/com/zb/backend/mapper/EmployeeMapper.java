package com.zb.backend.mapper;

import com.zb.backend.entity.Employee;
import com.zb.backend.model.request.RegisterRequest;

public interface EmployeeMapper {
    // 通过id获取员工信息
    Employee selectEmployeeByEmpId(Long empId);

    // 判断电话号码是否存在
    Boolean existsByPhone(String phone);

    // 判断身份证号是否存在
    Boolean existsByIdCard(String idCard);

    // 新增员工信息
    Boolean insertEmployee(RegisterRequest registerRequest);
}
