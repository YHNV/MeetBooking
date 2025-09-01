package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum EquipmentEnum implements ResultEnum{
    // 获取所有设备集合
    SUC_GET_EQUIP(2001, "获取成功"),
    ERR_GET_EQUIP(4001, "获取失败"),

    // 新增设备
    SUC_ADD_EQUIP(2001, "新增成功"),
    ERR_ADD_EQUIP(4001, "新增失败"),
    ERR_ADD_EXISTS(4002, "该设备已存在"),

    // 修改设备
    SUC_UPDATE_EQUIP(2001, "修改成功"),
    ERR_UPDATE_EQUIP(4001, "修改失败"),
    ERR_UPDATE_NOT_ID(4002, "设备ID不存在"),

    // 删除设备
    SUC_DELETE_EQUIP(2001, "删除成功"),
    ERR_DELETE_EQUIP(2001, "删除失败"),
    ERR_DELETE_ROOM_EQUIP(4002, "会议室设备删除错误"),

    // 删除暂时不做





    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    EquipmentEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
