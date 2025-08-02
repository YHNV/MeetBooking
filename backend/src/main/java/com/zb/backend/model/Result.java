package com.zb.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    // 返回操作成功的响应结果
    public static <T> Result<T> success(Integer code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    // 返回操作失败的响应结果
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

}
