package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum EmployeeEnum implements ResultEnum {
    // 修改员工信息
    SUC_UPDATE_INFO(2001, "修改成功"),
    ERR_UPDATE_INFO(4001, "修改失败"),
    ERR_UPDATE_POSITION(4002, "职位非法修改"),
    ERR_UPDATE_MANAGER(4003, "经理非法修改"),
    ERR_PHONE_DUPLICATE(4004, "电话号码已被占用"),
    ERR_IDCARD_DUPLICATE(4005, "身份证号码已被占用"),

    // 分页查询员工信息
    SUC_QUERY_INFO(2001, "查询成功"),
    ERR_QUERY_INFO(4001, "查询失败");

    private final Integer code;
    private final String message;

    EmployeeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
