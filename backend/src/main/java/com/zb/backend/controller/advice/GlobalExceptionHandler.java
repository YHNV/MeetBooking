package com.zb.backend.controller.advice;

import com.zb.backend.constants.enums.ErrorEnum;
import com.zb.backend.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理所有异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleException(Exception e) {
        String message = e.getClass().getName() + ": " + e.getMessage();
        System.out.println(message);
        MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
        String errorMsg = exception.getBindingResult()
                .getFieldError() // 获取第一个错误字段
                .getDefaultMessage(); // 获取你在注解中定义的message（如"邮箱长度不能超过63个字符"）

        // 输出到控制台（可选）
        System.out.println("校验错误：" + errorMsg);
        log.error(message);
        return Result.error(50001, errorMsg);
    }
}