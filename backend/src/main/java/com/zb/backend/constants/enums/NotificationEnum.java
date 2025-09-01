package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum NotificationEnum implements ResultEnum{
    // 获取通知
    SUC_GET_NOTIFY(2001, "获取成功"),
    ERR_GET_NOTIFY(4001, "获取失败"),

    // 获取未读数量
    SUC_GET_NOT_READ_NUM(2001, "获取成功"),
    ERR_GET_NOT_READ_NUM(4001, "获取失败"),

    ERR_NOT_EMP(4002, "非法请求"),

    // 全部已读
    SUC_ALL_READ(2001, "全部已读成功"),
    ERR_ALL_READ(4001, "全部已读失败"),



    // 统一报错
    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    NotificationEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
