package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum ReservationEnum implements ResultEnum{

    // 获取会议室状态
    // SUC_GET_AVAIL(2001, "获取成功"),
    // ERR_GET_AVAIL(2001, "获取成功"),


    // 统一报错
    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    ReservationEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
