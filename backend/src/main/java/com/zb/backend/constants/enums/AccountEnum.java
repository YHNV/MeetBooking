package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum AccountEnum implements ResultEnum {
    // 修改账号状态
    SUC_UPDATE_STATUS(2001, "修改成功"),
    ERR_UPDATE_STATUS(4001, "修改失败"),

    // 重置密码
    SUC_RESET_PASSWORD(2001, "重置成功"),
    ERR_RESET_PASSWORD(4001, "重置失败"),

    // 修改密码
    SUC_UPDATE_PASSWORD(2001, "修改成功"),
    ERR_OLD_PASSWORD(4001, "原密码错误"),
    ERR_UPDATE_PASSWORD(4002, "修改失败");

    private final Integer code;
    private final String message;

    AccountEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
