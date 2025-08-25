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
    ERR_ADD_NOT_EQUIP(4005, "存在无效设备ID"),

    // 修改会议室图片
    // SUC_UPDATE_IMG(2001, "修改成功"),
    // ERR_UPDATE_IMG(4001, "修改失败"),

    // 分页查询会议室
    SUC_QUERY_INFO(2001, "查询成功"),
    ERR_NOT_EQUIP(4001, "存在无效设备ID"),
    ERR_ILLEGAL_CAPACITY(4002, "非法容量参数"),
    ERR_QUERY_INFO(4003, "查询失败"),

    // 删除会议室
    // SUC_DELETE_ROOM(2001, "删除成功"),
    // ERR_DELETE_EXISTS(4001, "会议室不存在，无法删除"),
    // ERR_DELETE_ROOM(4002, "删除失败"),

    // 获取会议室设备集合
    SUC_EQUIP_ROOM(2001, "获取成功"),
    ERR_EXISTS_ROOM(4001, "该会议室不存在"),
    ERR_EQUIP_ROOM(4002, "获取失败"),

    // 修改会议室
    SUC_UPDATE_ROOM(2001, "修改成功"),
    ERR_UPDATE_ROOM(4001, "修改失败"),
    ERR_UPDATE_NAME(4002, "会议室名称已存在"),
    ERR_UPDATE_ID(4003, "会议室不存在"),
    ERR_UPDATE_EXISTS_EQUIP(4004, "存在无效设备ID"),
    ERR_UPDATE_DELETE_EQUIP(4005, "删除设备失败"),
    ERR_UPDATE_INSERT_EQUIP(4006, "会议室设备关联添加出错"),






    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    MeetingRoomEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
