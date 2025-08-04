package com.zb.backend.constants.enums;

import lombok.Getter;

// auth controller 登录返回的状态码和信息
@Getter
public enum AuthEnum implements ResultEnum {
    // 登录成功
    SUC(2001, "登录成功"),
    // 登录失败
    ERR_WRONG_CRED(4001, "账号或密码错误"), // CRED是CREDENTIAL（凭证）的简写
    ERR_ACC_LOCKED(4002, "账号已被冻结，不可登录"),
    ERR_ACC_NOT_EXIST(4003, "账号不存在");

    private final Integer code;
    private final String message;

    AuthEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
