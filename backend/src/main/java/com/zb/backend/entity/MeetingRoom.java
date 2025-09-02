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

    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("会议室id：").append(roomId).append("\n")
                .append("会议室名称：").append(roomName).append("\n")
                .append("会议室类型：").append(roomType).append("\n")
                .append("容纳人数：").append(capacity).append("\n")
                .append("位置：").append(location).append("\n")
                .append("状态：").append(roomStatus).append("\n");
        return sb.toString();
    }
}
