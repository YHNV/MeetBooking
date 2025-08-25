package com.zb.backend.model.response;

import com.zb.backend.entity.Equipment;
import com.zb.backend.entity.enums.RoomStatus;
import com.zb.backend.entity.enums.RoomType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QueryMeetingRoomsResponse {
    private Long roomId;
    private String roomName;
    private RoomType roomType;
    private Integer capacity;
    private String location;
    private RoomStatus roomStatus;
    private String imageUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 通过关联表查询出来的设备集合
    private List<Equipment> equipmentList;
}
