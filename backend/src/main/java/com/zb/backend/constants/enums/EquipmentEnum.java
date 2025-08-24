package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum EquipmentEnum implements ResultEnum{
    // 获取所有设备集合
    SUC_GET_EQUIP(2001, "获取成功"),
    ERR_GET_EQUIP(4001, "获取失败"),





    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    EquipmentEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
