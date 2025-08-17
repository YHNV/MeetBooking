package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum implements ResultEnum{
    // 统一报错
    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    ErrorEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
