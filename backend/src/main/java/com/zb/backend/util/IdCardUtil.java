package com.zb.backend.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * 身份证信息解析工具类
 */
public class IdCardUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 根据身份证号计算年龄
     * @param idCard 18位身份证号码
     * @return 年龄（整数）
     * @throws IllegalArgumentException 如果身份证格式不正确
     */
    public static Integer calculateAge(String idCard) {
        validateIdCard(idCard);

        String birthDateStr = idCard.substring(6, 14);
        LocalDate birthDate = LocalDate.parse(birthDateStr, DATE_FORMATTER);
        LocalDate currentDate = LocalDate.now();

        return Period.between(birthDate, currentDate).getYears();
    }

    /**
     * 根据身份证号解析性别
     * @param idCard 18位身份证号码
     * @return 性别："男" 或 "女"
     * @throws IllegalArgumentException 如果身份证格式不正确
     */
    public static String parseGender(String idCard) {
        validateIdCard(idCard);

        // 获取第17位数字（性别标识位）
        char genderChar = idCard.charAt(16);
        int genderCode = Character.getNumericValue(genderChar);

        // 奇数表示男性，偶数表示女性
        return genderCode % 2 == 1 ? "男" : "女";
    }

    /**
     * 根据身份证号解析带年份的出生日期
     * @param idCard 18位身份证号码
     * @return 格式化的出生日期，如："1990年2月9日"
     * @throws IllegalArgumentException 如果身份证格式不正确
     */
    public static String parseBirthdayWithYear(String idCard) {
        validateIdCard(idCard);

        String year = idCard.substring(6, 10);
        String month = idCard.substring(10, 12);
        String day = idCard.substring(12, 14);

        // 去除月份和日期的前导零
        month = String.valueOf(Integer.parseInt(month));
        day = String.valueOf(Integer.parseInt(day));

        return year + "年" + month + "月" + day + "日";
    }

    /**
     * 验证身份证格式
     * @param idCard 身份证号码
     * @throws IllegalArgumentException 如果身份证格式不正确
     */
    private static void validateIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            throw new IllegalArgumentException("身份证号码必须为18位");
        }

        // 简单验证：前17位必须是数字
        for (int i = 0; i < 17; i++) {
            if (!Character.isDigit(idCard.charAt(i))) {
                throw new IllegalArgumentException("身份证前17位必须为数字");
            }
        }

        // 验证出生日期是否合法
        String birthDateStr = idCard.substring(6, 14);
        try {
            LocalDate.parse(birthDateStr, DATE_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("身份证中的出生日期不合法");
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        String idCard = "110102199002090075";

        try {
            System.out.println("年龄: " + calculateAge(idCard) + "岁");
            System.out.println("性别: " + parseGender(idCard));
            System.out.println("出生日期: " + parseBirthdayWithYear(idCard));
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
}
