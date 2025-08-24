package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum MeetingRoomEnum implements ResultEnum{
    // 新增会议室
    SUC_ADD_ROOM(2001, "添加成功"),
    ERR_ADD_ROOM(4001, "添加失败"),
    ERR_EXISTS_NAME(4002, "该会议室已存在"),
    ERR_DELETE_EQUIP(4003, "删除设备关联失败"),
    ERR_ROOM_EQUIP(4004, "会议室设备关联添加出错"),

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
