package com.zb.backend.entity;

import com.zb.backend.entity.enums.RoomStatus;
import com.zb.backend.entity.enums.RoomType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeetingRoom {
    private Long roomId;
    private String roomName;
    private RoomType roomType;
    private Integer capacity;
    private String location;
    private RoomStatus roomStatus;
    private String imageUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
