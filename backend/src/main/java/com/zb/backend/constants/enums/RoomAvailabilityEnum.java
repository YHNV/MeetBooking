package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum RoomAvailabilityEnum implements ResultEnum{

    // 获取会议室状态
    SUC_GET_AVAIL(2001, "获取成功"),
    ERR_GET_AVAIL(4001, "获取失败"),
    ERR_GET_NOT_EXISTS_ROOM(4002, "会议室不存在"),


    // 统一报错
    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    RoomAvailabilityEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
