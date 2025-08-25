package com.zb.backend.mapper;

import com.zb.backend.entity.MeetingRoom;
import com.zb.backend.model.request.AddMeetingRoomRequest;
import com.zb.backend.model.request.QueryMeetingRoomsRequest;
import com.zb.backend.model.response.QueryMeetingRoomsResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface MeetingRoomMapper {
    // 新增会议室
    Boolean insertMeetingRoom(AddMeetingRoomRequest addMeetingRoomRequest);

    // 根据名字查询会议室
    MeetingRoom selectMeetingRoomByRoomName(@NotBlank(message = "会议室名称不能为空") @Size(max = 31, message = "会议室名称长度不能超过31个字符") String roomName);

    // 查询总记录数
    Integer countMeetingRoomList(QueryMeetingRoomsRequest queryRequest);

    // 分页查询会议室信息
    List<MeetingRoom> selectMeetingRoomList(QueryMeetingRoomsRequest queryRequest);
}
