package com.zb.backend.util;

import com.zb.backend.constants.enums.ResultEnum;
import java.util.Arrays;

public class EnumUtil {

    /**
     * 根据消息查找对应的 ResultEnum 枚举实例
     * @param message 枚举的 message
     * @param enumClasses 可能的枚举类（实现了 ResultEnum 的类）
     * @return 匹配的枚举实例，未找到返回 null
     */
    @SafeVarargs
    public static ResultEnum findByMessage(String message, Class<? extends ResultEnum>... enumClasses) {
        for (Class<? extends ResultEnum> enumClass : enumClasses) {
            // 遍历枚举类的所有枚举实例
            ResultEnum[] enums = enumClass.getEnumConstants();
            if (enums == null) continue;

            // 匹配消息
            ResultEnum matchedEnum = Arrays.stream(enums)
                    .filter(e -> e.getMessage().equals(message))
                    .findFirst()
                    .orElse(null);

            if (matchedEnum != null) {
                return matchedEnum;
            }
        }
        return null;
    }
}