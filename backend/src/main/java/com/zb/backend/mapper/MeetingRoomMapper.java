package com.zb.backend.mapper;

import com.zb.backend.entity.MeetingRoom;
import com.zb.backend.model.request.AddMeetingRoomRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public interface MeetingRoomMapper {
    // 新增会议室
    Boolean insertMeetingRoom(AddMeetingRoomRequest addMeetingRoomRequest);

    // 根据名字查询会议室
    MeetingRoom selectMeetingRoomByRoomName(@NotBlank(message = "会议室名称不能为空") @Size(max = 31, message = "会议室名称长度不能超过31个字符") String roomName);


}
