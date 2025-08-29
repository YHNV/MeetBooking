package com.zb.backend.service;

import com.zb.backend.constants.enums.RoomAvailabilityEnum;
import com.zb.backend.entity.MeetingRoom;
import com.zb.backend.entity.RoomAvailability;
import com.zb.backend.mapper.RoomAvailabilityMapper;
import com.zb.backend.model.TimeSlot;
import com.zb.backend.model.request.GetRoomAvailTimeSlotListRequest;
import com.zb.backend.model.response.GetRoomAvailDateListResponse;
import com.zb.backend.model.response.GetRoomAvailTimeSlotListResponse;
import com.zb.backend.util.RoomTimeSlotUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    // 获取当前会议室可用日期
    public GetRoomAvailDateListResponse getRoomAvailDateList(Long roomId) {
        // 首先查找会议室是否存在
        MeetingRoom meetingRoom = meetingRoomService.getRoomByRoomId(roomId);
        if (meetingRoom == null) {
            throw new RuntimeException(RoomAvailabilityEnum.ERR_GET_NOT_EXISTS_ROOM.getMessage());
        }

        return new GetRoomAvailDateListResponse(roomId, roomAvailabilityMapper.selectRoomAvailDateByRoomId(roomId)) ;
    }

    // 获取会议室可预约时间段
    public GetRoomAvailTimeSlotListResponse getRoomAvailTimeSlotList(GetRoomAvailTimeSlotListRequest request) {
        // 首先查找会议室是否存在
        MeetingRoom meetingRoom = meetingRoomService.getRoomByRoomId(request.getRoomId());
        if (meetingRoom == null) {
            throw new RuntimeException(RoomAvailabilityEnum.ERR_GET_NOT_EXISTS_ROOM.getMessage());
        }
        // 查询状态值
        RoomAvailability roomAvail = roomAvailabilityMapper.selectRoomAvailStatus(request.getRoomId(), request.getDate());
        if (roomAvail == null) {
            throw new RuntimeException(RoomAvailabilityEnum.ERR_GET_NOT_EXISTS_DATE.getMessage());
        }
        // 通过状态值，获取可用预约时间段
        List<TimeSlot> timeSlotList = RoomTimeSlotUtil.parseStatusToTimeSlots(roomAvail.getSlotStatus());
        // 封装进响应模型，并返回
        return new GetRoomAvailTimeSlotListResponse(
                request.getRoomId(),
                request.getDate(),
                roomAvail.getSlotStatus(),
                timeSlotList
        );
    }

    // 根据roomId和date获取int状态值
    public RoomAvailability selectRoomAvailStatus(Long roomId, LocalDate reservationDate) {
        return roomAvailabilityMapper.selectRoomAvailStatus(roomId, reservationDate);
    }

    // 修改预约时段状态值
    public Boolean updateStatusByIdAndDate(Long roomId, LocalDate reservationDate, Integer newStatus) {
        return roomAvailabilityMapper.updateStatusByIdAndDate(roomId, reservationDate, newStatus);
    }

    // 给新增的会议室添加预约状态
    public Boolean addRoomAvail(Long roomId) {
        return roomAvailabilityMapper.insertRoomAvailByRoomId(roomId);
    }
}
