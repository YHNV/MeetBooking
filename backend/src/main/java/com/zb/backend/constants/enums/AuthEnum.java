package com.zb.backend.constants.enums;

import lombok.Getter;

// auth controller 登录返回的状态码和信息
@Getter
public enum AuthEnum implements ResultEnum {
    // 登录相关的枚举常量
    SUC_LOGIN(2001, "登录成功"),
    ERR_WRONG_PWD(4001, "密码错误"),
    ERR_ACC_LOCKED(4002, "账号已被冻结，不可登录"),
    ERR_ACC_NOT_EXIST(4003, "账号不存在"),

    // 获取用户信息相关
    SUC_INFO(2001, "获取用户信息成功"),
    ERR_NO_LOGIN(4001, "用户未登录或令牌无效"),
    ERR_INFO_NOT_EXIST(4002, "用户信息不存在"),

    // 退出登录相关
    SUC_LOGOUT(2001, "退出登录成功"),
    ERR_LOGOUT(4001, "令牌失效，请重新登录"),

    // 注册相关
    SUC_REGISTER(2001, "注册成功"),
    ERR_PHONE_DUPLICATE(4001, "电话号码已被占用"),
    ERR_IDCARD_DUPLICATE(4002, "身份证号码已被占用"),
    ERR_DEPT_NOT_EXIST(4003, "部门ID不存在"),
    ERR_NO_PERMISSION(4004, "无权限，无法执行操作");

    private final Integer code;
    private final String message;

    AuthEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
