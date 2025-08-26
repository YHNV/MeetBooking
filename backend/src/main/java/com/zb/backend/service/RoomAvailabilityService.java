package com.zb.backend.service;

import com.zb.backend.constants.enums.RoomAvailabilityEnum;
import com.zb.backend.entity.MeetingRoom;
import com.zb.backend.entity.RoomAvailability;
import com.zb.backend.mapper.RoomAvailabilityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomAvailabilityService {
    private final RoomAvailabilityMapper roomAvailabilityMapper;
    private final MeetingRoomService meetingRoomService;

    public List<RoomAvailability> getRoomAvailability(Long roomId) {
        // 首先查找会议室是否存在
        MeetingRoom meetingRoom = meetingRoomService.getRoomByRoomId(roomId);

        if (meetingRoom == null) {
            throw new RuntimeException(RoomAvailabilityEnum.ERR_GET_NOT_EXISTS_ROOM.getMessage());
        }

        // 获取会议室可用状态
        List<RoomAvailability> availabilityList = roomAvailabilityMapper.selectAvailabilityByRoomId(roomId);

        return availabilityList;
    }
}
