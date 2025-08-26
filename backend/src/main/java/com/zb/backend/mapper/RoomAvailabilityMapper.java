package com.zb.backend.mapper;

import com.zb.backend.entity.RoomAvailability;

import java.util.List;

public interface RoomAvailabilityMapper {
    // 通过roomId获取集合
    List<RoomAvailability> selectAvailabilityByRoomId(Long roomId);
}
