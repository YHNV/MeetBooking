package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum DepartmentEnum implements ResultEnum {
    // 获取简单部门集合
    SUC_SIMPLE_LIST(2001, "获取成功"),
    ERR_SIMPLE_LIST(4001, "获取失败"),

    // 获取全部部门信息
    SUC_GET_ALL(2001, "获取成功"),
    ERR_GET_ALL(4001, "获取失败"),

    // 新增部门
    SUC_ADD_DEPT(2001, "新增成功"),
    ERR_ADD_DEPT(4001, "新增失败"),
    ERR_DEPT_NAME(4002, "部门名称已存在"),
    ERR_EMP_ID(4003, "员工不存在"),

    // 修改部门
    SUC_UPDATE_DEPT(2001, "修改成功"),
    ERR_UPDATE_DEPT(4001, "修改失败"),
    ERR_EXISTS_DEPT(4002, "部门不存在"),

    // 分页查询部门信息
    // SUC_QUERY_INFO(2001, "查询成功"),
    // ERR_QUERY_INFO(4001, "查询失败");

    ERROR(50001, "系统异常");

    private final Integer code;
    private final String message;

    DepartmentEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
