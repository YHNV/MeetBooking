package com.zb.backend.constants.enums;

import lombok.Getter;

@Getter
public enum UploadEnum implements ResultEnum{
    // 上传图片
    SUC_IMAGE_UPLOAD(2001, "上传图片成功"),
    ERR_IMAGE_UPLOAD(4001, "上传图片失败"),

    // 统一报错
    ERR_SERVER(5001, "服务器异常");

    private final Integer code;
    private final String message;

    UploadEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
