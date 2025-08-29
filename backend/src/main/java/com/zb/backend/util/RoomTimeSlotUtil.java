package com.zb.backend.util;

import com.zb.backend.model.TimeSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomTimeSlotUtil {
    // 时间槽开始时间数组
    private static final String[] START_TIMES = {
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30"
    };

    // 时间槽结束时间数组，与开始时间一一对应
    private static final String[] END_TIMES = {
            "09:30", "10:00", "10:30", "11:00", "11:30", "12:00",
            "12:30", "13:00", "13:30", "14:00", "14:30", "15:00",
            "15:30", "16:00", "16:30", "17:00"  // 最后一个结束时间为17:00
    };

    // 存储索引到TimeSlot的映射关系
    private static final Map<Integer, TimeSlot> INDEX_TO_TIMESLOT;

    // 静态初始化块，初始化映射关系
    static {
        INDEX_TO_TIMESLOT = new HashMap<>(START_TIMES.length);
        for (int i = 0; i < START_TIMES.length; i++) {
            INDEX_TO_TIMESLOT.put(i, new TimeSlot(START_TIMES[i], END_TIMES[i]));
        }
    }

    /**
     * 根据状态值解析可用的时间槽列表
     * @param status 预约状态的int值
     * @return 可用的时间槽列表
     */
    public static List<TimeSlot> parseStatusToTimeSlots(int status) {
        List<TimeSlot> availableSlots = new ArrayList<>();

        // 遍历所有16个时间槽
        for (int i = 0; i < START_TIMES.length; i++) {
            // 检查第i位是否为0（0表示可用）
            if ((status & (1 << i)) == 0) {
                availableSlots.add(INDEX_TO_TIMESLOT.get(i));
            }
        }

        return availableSlots;
    }

    /**
     * 将单个时间区间转换为整数状态值
     * @param timeSlot 时间区间对象
     * @return 整数状态值
     */
    public static int convertTimeSlotToStatus(TimeSlot timeSlot) {
        return convertTimeSlotsToStatus(List.of(timeSlot));
    }

    /**
     * 将时间区间列表转换为整数状态值
     * @param timeSlots 时间区间列表
     * @return 整数状态值
     */
    public static int convertTimeSlotsToStatus(List<TimeSlot> timeSlots) {
        int status = 0;

        for (TimeSlot slot : timeSlots) {
            // 找到开始时间和结束时间对应的索引
            int startIndex = findTimeIndex(slot.getStartTime(), true);
            int endIndex = findTimeIndex(slot.getEndTime(), false);

            System.out.println("开始时间索引：" + startIndex);
            System.out.println("结束时间索引：" + endIndex);

            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                // 标记从startIndex到endIndex-1的所有位为1
                for (int i = startIndex; i <= endIndex; i++) {
                    status |= (1 << i);
                }
            }
        }

        return status;
    }

    /**
     * 查找时间点在时间数组中的索引
     * @param time 时间点字符串（格式："HH:mm"）
     * @param isStartTime 是否是开始时间
     * @return 索引值，如果找不到返回-1
     */
    private static int findTimeIndex(String time, boolean isStartTime) {
        String[] timeArray = isStartTime ? START_TIMES : END_TIMES;
        for (int i = 0; i < timeArray.length; i++) {
            if (timeArray[i].equals(time)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取所有可用的时间区间定义（用于前端展示等）
     * @return 所有时间区间列表
     */
    public static List<TimeSlot> getAllTimeSlots() {
        List<TimeSlot> allSlots = new ArrayList<>();
        for (int i = 0; i < START_TIMES.length; i++) {
            allSlots.add(new TimeSlot(START_TIMES[i], END_TIMES[i]));
        }
        return allSlots;
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 测试所有时间区间
        System.out.println("所有可用时间区间:");
        getAllTimeSlots().forEach(System.out::println);

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 测试用例1：状态值为3（二进制11），表示前两个时间段被占用
        int status1 = 3;
        System.out.println("状态值 " + status1 + " 对应的可用时间段:");
        List<TimeSlot> slots1 = parseStatusToTimeSlots(status1);
        slots1.forEach(System.out::println);

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 测试用例1：状态值为14267（二进制0011011110111011），表示前两个时间段被占用
        int status2 = 1023;
        System.out.println("状态值 " + status2 + " 对应的可用时间段:");
        List<TimeSlot> slots2 = parseStatusToTimeSlots(status2);
        slots2.forEach(System.out::println);

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 测试用例2：单个时间区间转换为状态值
        TimeSlot singleSlot = new TimeSlot("09:00", "11:00");
        int singleStatus = convertTimeSlotToStatus(singleSlot);
        System.out.println("单个时间区间 " + singleSlot + " 转换后的状态值: " + singleStatus);
        System.out.println("二进制表示: " + Integer.toBinaryString(singleStatus));

        // 测试用例3：多个时间区间
        TimeSlot slot1 = new TimeSlot("09:00", "10:00"); // 占用0,1位
        TimeSlot slot2 = new TimeSlot("11:00", "12:00"); // 占用4,5位
        int multiStatus = convertTimeSlotsToStatus(List.of(slot1, slot2));
        System.out.println("多个时间区间转换后的状态值: " + multiStatus);
        System.out.println("二进制表示: " + Integer.toBinaryString(multiStatus));
    }
}
