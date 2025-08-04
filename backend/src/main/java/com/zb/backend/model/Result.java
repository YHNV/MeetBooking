package com.zb.backend.model;

import com.zb.backend.constants.enums.ResultEnum;
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

    // 使用枚举返回成功的响应结果
    public static <T> Result<T> success(ResultEnum resultEnum, T data) {
        return new Result<>(resultEnum.getCode(), resultEnum.getMessage(), data);
    }

    // 返回操作失败的响应结果，带数据
    public static <T> Result<T> error(Integer code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    // 返回操作失败的响应结果，不带数据
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    // 使用枚举返回失败的响应结果，带数据
    public static <T> Result<T> error(ResultEnum resultEnum, T data) {
        return new Result<>(resultEnum.getCode(), resultEnum.getMessage(), data);
    }

    // 使用枚举返回失败的响应结果，不带数据
    public static <T> Result<T> error(ResultEnum resultEnum) {
        return new Result<>(resultEnum.getCode(), resultEnum.getMessage(), null);
    }

}
