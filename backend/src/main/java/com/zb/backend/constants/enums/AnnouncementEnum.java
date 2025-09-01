package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum AnnouncementEnum implements ResultEnum{
    // 新增公告
    SUC_ADD_ANN(2001, "新增公告成功"),
    ERR_ADD_ANN(4001, "新增公告失败"),

    // 查询公告
    SUC_GET_ANN(2001, "查询成功"),
    ERR_GET_ANN(4001, "查询失败"),

    // 修改公告
    SUC_UPDATE_ANN(2001, "修改成功"),
    ERR_UPDATE_ANN(4001, "修改失败"),

    ERR_EXISTS_ID(4002, "公告ID不存在"),

    // 删除公告
    SUC_DELETE_ANN(2001, "删除成功"),
    ERR_DELETE_ANN(4001, "删除失败"),




    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    AnnouncementEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
