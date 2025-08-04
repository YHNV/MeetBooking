package com.zb.backend.constants.enums;

// 定义一个枚举接口，所有返回枚举类都要实现这个方法，这样才能被Result统一调用
public interface ResultEnum {
    Integer getCode();
    String getMessage();
}
