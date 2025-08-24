package com.zb.backend.service;

import com.zb.backend.constants.enums.MeetingRoomEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.MeetingRoom;
import com.zb.backend.mapper.MeetingRoomMapper;
import com.zb.backend.model.request.AddMeetingRoomRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {
    private final MeetingRoomMapper meetingRoomMapper;

    // 新增会议室
    public ResultEnum addMeetingRoom(@Valid AddMeetingRoomRequest addMeetingRoomRequest) {
        // 首先根据会议室名字查询，会议室是否存在
        Boolean existsRoom = meetingRoomMapper.selectMeetingRoomByRoomName(addMeetingRoomRequest.getRoomName());

        if (existsRoom) {
            // 该会议室已存在
            return MeetingRoomEnum.ERR_EXISTS_NAME;
        }
        // 会议室不存在，执行插入语句
        Boolean addRoom = meetingRoomMapper.insertMeetingRoom(addMeetingRoomRequest);

        if (!addRoom) {
            // 插入失败
            return MeetingRoomEnum.ERR_ADD_ROOM;
        }

        return MeetingRoomEnum.SUC_ADD_ROOM;

    }
}
