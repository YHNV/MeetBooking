package com.zb.backend.service;

import com.zb.backend.entity.Employee;
import com.zb.backend.mapper.EmployeeMapper;
import com.zb.backend.model.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
