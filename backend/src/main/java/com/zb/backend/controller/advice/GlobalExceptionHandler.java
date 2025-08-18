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

        log.error(message, e); // 打印完整堆栈信息，方便调试
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