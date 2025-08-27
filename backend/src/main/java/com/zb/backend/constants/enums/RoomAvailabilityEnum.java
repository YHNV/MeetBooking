package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum RoomAvailabilityEnum implements ResultEnum{

    // 获取会议室状态
    SUC_GET_AVAIL(2001, "获取成功"),
    ERR_GET_AVAIL(4001, "获取失败"),

    // 会议室不存在
    ERR_GET_NOT_EXISTS_ROOM(4002, "会议室不存在"),
    ERR_GET_NOT_EXISTS_DATE(4003, "日期不存在"),

    // 获取会议室可用日期集合
    SUC_GET_AVAIL_DATE(2001, "获取成功"),
    ERR_GET_AVAIL_DATE(4001, "获取失败"),

    // 获取会议室可用状态集合
    SUC_GET_AVAIL_STATUS(2001, "获取成功"),
    ERR_GET_AVAIL_STATUS(4001, "获取失败"),

    // 获取全部预约时间段
    SUC_GET_ALL_TIME_SLOT(2001, "获取成功"),


    // 统一报错
    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    RoomAvailabilityEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
