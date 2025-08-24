package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum MeetingRoomEnum implements ResultEnum{
    // 新增会议室
    SUC_ADD_ROOM(2001, "添加成功"),
    ERR_ADD_ROOM(4001, "添加失败"),
    ERR_EXISTS_NAME(4003, "该会议室已存在"),

    // 修改会议室图片
    SUC_UPDATE_IMG(2001, "修改成功"),
    ERR_UPDATE_IMG(4001, "修改失败"),





    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    MeetingRoomEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
