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
    SUC_INFO(2002, "获取用户信息成功"),
    ERR_NO_LOGIN(4004, "用户未登录或令牌无效"),
    ERR_INFO_NOT_EXIST(4005, "用户信息不存在");

    private final Integer code;
    private final String message;

    AuthEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
