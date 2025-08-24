package com.zb.backend.controller.advice;

import com.zb.backend.constants.enums.ErrorEnum;
import com.zb.backend.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 处理文件上传大小超限异常（优先级高于通用异常）
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 使用400错误码，更符合客户端错误语义
    public Result<Object> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        // 自定义错误信息，明确告知用户限制
        String errorMsg = "文件大小超过限制，最大支持4MB";
        // 打印错误日志（使用log更规范）
        System.out.println(errorMsg);
        // log.error("文件上传大小超限：{}", e.getMessage());
        // 返回自定义错误响应
        return Result.error(400, errorMsg);
    }

    // 处理IOException
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 使用400错误码，更符合客户端错误语义
    public Result<Object> handleIOException(IOException e) {
        String errorMsg = e.getMessage();
        System.out.println(errorMsg);
        // 打印错误日志（保留原始异常信息便于排查）
        // log.error("业务异常：{}", errorMsg, e);
        // 如果异常消息包含".", 替换为友好提示
        if (errorMsg != null && errorMsg.contains(".")) {
            errorMsg = "系统异常";
        }
        // 返回异常消息给前端
        return Result.error(400, errorMsg);
    }

    // 处理校验异常
    // 1. 优先处理参数校验异常（MethodArgumentNotValidException）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400状态码，符合参数错误语义
    public Result<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        // 获取第一个校验失败的message（也可收集所有错误）
        String errorMsg = e.getBindingResult()
                .getFieldError() // 获取第一个错误字段
                .getDefaultMessage(); // 提取注解中的message

        // 打印错误日志（便于排查问题）
        System.out.println("参数校验错误：" + errorMsg);

        // 返回校验错误信息（使用400类错误码，区分系统异常）
        return Result.error(400, errorMsg);
    }

    // 3. 处理自定义消息的RuntimeException（新增）
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 按业务错误类型选择合适的状态码
    public Result<Object> handleRuntimeException(RuntimeException e) {
        // 直接提取异常中携带的消息（即MeetingRoomEnum.ERR_EXISTS_NAME.getMessage()的值）
        String errorMsg = e.getMessage();
        System.out.println(errorMsg);
        // 打印错误日志（保留原始异常信息便于排查）
        // log.error("业务异常：{}", errorMsg, e);
        // 如果异常消息包含".", 替换为友好提示
        if (errorMsg != null && errorMsg.contains(".")) {
            errorMsg = "系统异常";
        }
        // 返回异常消息给前端
        return Result.error(400, errorMsg);
    }

    // 处理其他未捕获异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleException(Exception e) {
        String message = e.getClass().getName() + ": " + e.getMessage();
        System.out.println(message);

        // 先判断异常是否为参数校验异常
        // if (e instanceof MethodArgumentNotValidException) {
        //     // 是参数校验异常，才进行转换
        //     MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
        //     String errorMsg = exception.getBindingResult()
        //             .getFieldError() // 获取第一个错误字段
        //             .getDefaultMessage(); // 获取注解中定义的提示信息
        //     System.out.println("校验错误：" + errorMsg);
        //     return Result.error(5001, errorMsg);
        // } else {
            // 不是参数校验异常，返回通用错误信息
            return Result.error(500, "系统异常");
        // }
    }
}