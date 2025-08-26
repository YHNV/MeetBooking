package com.zb.backend.mapper;

import com.zb.backend.entity.RoomAvailability;

import java.time.LocalDate;
import java.util.List;

public interface RoomAvailabilityMapper {
    // 通过roomId获取集合
    List<RoomAvailability> selectAvailabilityByRoomId(Long roomId);


    // 获取会议室可用日期
    List<LocalDate> selectRoomAvailDateByRoomId(Long roomId);
}
