package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum ReservationEnum implements ResultEnum{

    // 预约会议室
    SUC_RESERVATION(2001, "预约成功"),
    ERR_RESERVATION(4001, "预约失败"),
    ERR_RES_ADMIN(4002, "非法预约"),
    ERR_EXISTS_ROOM(4003, "会议室不存在"),
    ERR_EXISTS_DATE(4004, "日期不存在"),
    ERR_STATUS_ROOM(4005, "会议室不可用"),
    ERR_TIME_SLOT(4006, "开始时间不能晚于结束时间"),
    ERR_NOT_MANAGER_MENTION(4007, "非经理不能@全体员工"),
    ERR_NOT_MANAGER_ROOM(4008, "非经理不能预约大型会议室"),
    ERR_ROOM_ATTENDEE(4009, "参与会议室异常"),
    ERR_RES_TIME_CONFLICT(4010, "预约时间段冲突"),
    ERR_UPDATE_STATUS(4011, "更新预约状态异常"),
    ERR_NOTIFICATION(4012, "通知提醒异常"),
    ERR_EXISTS_EMP(4013, "存在无效人员："),
    ERR_DEPT_EMP(4014, "部门不存在该员工："),

    // 查询预约
    SUC_QUERY_RES(2001, "查询成功"),
    ERR_QUERY_RES(4001, "查询失败"),
    // ERR_(4001, "查询失败"),




    // 统一报错
    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    ReservationEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
